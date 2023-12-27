package loom.component;

import componentArchitecture.Component;
import componentArchitecture.*;
import entityInfoGui.PopUpInfoGui;
import gameManaging.GameManager;
import instances.Entity;
import loom.LoomMod;
import loom.entity.Classifiable;
import loom.entity.weaver.EntityPrint;
import loom.equilinox.EvolutionRequirement;
import loom.equilinox.ducktype.ComponentReference;
import loom.equilinox.vanilla.Key;
import loom.equilinox.vanilla.VanillaColor;
import org.lwjgl.util.vector.Vector3f;
import speciesInformation.SpeciesInfoType;
import userInterfaces.TextStatInfo;
import utils.BinaryReader;
import utils.BinaryWriter;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The component responsible for adding specific behaviors, interactions, and actions to entities.
 * Entities often share common components, such as the {@code Spreader} component, which imparts
 * biome-spreading behavior to plants. However, these components may have varying parameters.
 * In the case of {@code Spreader}, it differentiates the specific biome that the entity spreads.
 *
 * <p>
 * To add a new parameter to the custom component, users must create a constructor.
 * The allowable parameter types are: integers, floats, integer arrays, float arrays, {@link Vector3f}s,
 * booleans, longs, enums, {@link Classifiable}s, strings (excluding semicolons),
 * or also maps of allowable parameters as key and values.
 * </p>
 *
 * <p>
 * Additionally, some parameters may be optional. For example, the hiding spot in the {@code Flee} component is
 * null by default. Users must create additional constructors with extra parameters and explicitly register
 * all optionals at {@link #create(ComponentRequester) create} method. The parameters must be registered in order.
 * </p>
 */
public abstract class LoomComponent extends Component implements ComponentReference, PrintableComponent {
    private final List<PopUpInfoGui> info = new ArrayList<>();
    private final List<Action> actions = new ArrayList<>();
    private final List<ControlBehaviour> controllableBehaviours = new ArrayList<>();

    private final Map<TextStatInfo, Predicate<LoomComponent>> performance = new HashMap<>();

    private final boolean active;

    final List<EvolutionRequirement> evolutionRequirements = new ArrayList<>();

    final ComponentRequester requester = new ComponentRequester();

    private LoomBlueprint loader = null;
    private Set<PrintableComponent> requirements;

    /*TODO SUPPORT CUSTOM TABS*/
    final Map<SpeciesInfoType, String> speciesInfo = new HashMap<>();

    final ComponentParamRegistry registry;

    public LoomComponent() {
        super(null);
        this.registry = new ComponentParamRegistry(getClass());
        register(registry);
        this.active = isUpdateOverridden();
    }

    private boolean isUpdateOverridden() {
        try {
            Method method = this.getClass().getDeclaredMethod("update", float.class);
            Method superMethod = LoomComponent.class.getMethod("update", float.class);

            return !superMethod.equals(method);
        } catch (NoSuchMethodException e) {
            return true;
        }
    }

    @Override
    public final void getStatusInfo(List<PopUpInfoGui> info) {
        info.addAll(this.info);
    }

    @Override
    public final void getActions(List<Action> actions) {
        actions.addAll(this.actions);
    }

    @Override
    public final void getPerformanceBuffsInfo(List<TextStatInfo> info) {
        performance.forEach((txt, func) -> {
            if (func.test(this)) info.add(txt);
        });
    }

    @Override
    public final void getControlableBehaviour(List<ControlBehaviour> behaviours) {
        behaviours.addAll(controllableBehaviours);
    }

    protected final void addToStatisticsInfoTab(PopUpInfoGui info) {
        this.info.add(info);
    }

    protected final void addToActionInfoTab(Action action) {
        this.actions.add(action);
    }

    /**
     * Adds information to the performance buffs information tab.
     *
     * @param name          The name of the performance buff.
     * @param nameColor     The color of the name, or {@code null} for default color.
     * @param value         The value associated with the performance buff.
     * @param valueColor    The color of the value, or {@code null} for default color.
     * @param description   The description of the performance buff.
     * @param func          A function to determine if the performance buff is applicable to this {@link LoomComponent}.
     *                      The function is checked every game tick to ensure the performance buff should be on.
     */
    protected final void addToPerformanceBuffsInfoTab(String name, @Nullable Color nameColor,
                                                      String value, @Nullable Color valueColor,
                                                      String description, Predicate<LoomComponent> func) {
        if (nameColor == null) nameColor = Color.WHITE;
        if (valueColor == null) valueColor = VanillaColor.BEIGE.color;
        performance.put(new TextStatInfo(name, value,
                VanillaColor.parseColor(nameColor),
                VanillaColor.parseColor(valueColor),
                description), func);
    }

    protected final void addToGeneralShopTab(String label, String value) {
        speciesInfo.put(SpeciesInfoType.GENERAL, label + LoomMod.MOD_POINTER + value);
    }

    protected final void addToHabitatShopTab(String label, String value) {
        speciesInfo.put(SpeciesInfoType.PREFERENCES, label + LoomMod.MOD_POINTER + value);
    }

    protected final void addToAbilitiesShopTab(String label, String value) {
        speciesInfo.put(SpeciesInfoType.ABILITIES, label + LoomMod.MOD_POINTER + value);
    }

    protected final void addControllableBehaviour(String name, Key key, Runnable run) {
        controllableBehaviours.add(new ControlBehaviour(name, key.getOrdinal(), false) {
            @Override public void doAction() { run.run(); }
        });
    }

    @Override
    public void export(BinaryWriter writer) throws IOException {
        for (Field field : registry.dynamicFields) {
            field.setAccessible(true);
            try {
                if (!EntityPrint.writeBinaryType(writer, field.get(this)))
                    System.err.println("Field " + field.getName() + " is a type supported by the saving engine. " +
                            "Supported types are: integers, floats, longs, shorts, Vector3f's, booleans and strings.");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void create(ComponentBundle bundle) {
        requester.setBundle(bundle); create(requester);
        requirements = requester.requirements;
        LoomParams params = (LoomParams) bundle.getParameters(this.getType());
        if (params != null) params.getFields().forEach((f, o) -> {
            try {
                f.setAccessible(true);
                f.set(this, o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public boolean reproduce(ParamsBundle params, boolean selected, Entity entity) {
        params.addParams(new LoomParams(this));
        return true;
    }

    @Override
    public void load(ComponentBundle bundle, BinaryReader reader) throws Exception {
        create(bundle); for (Field field : registry.dynamicFields) {
            field.set(this, EntityPrint.readBinaryType(reader, field.getType()));
        }
    }

    public final ComponentLoader getLoader() {
        return loader;
    }

    protected abstract void create(ComponentRequester requester);
    protected abstract void register(ComponentParamRegistry registry);

    /**
     * Dynamic components can change the model of the entity constantly within runtime.
     * Components like Animation, Movement and Flee are dynamic.
     */
    public abstract boolean isDynamic();

    @Override
    public boolean isActive() {
        return active;
    }

    public Set<PrintableComponent> getRequirements() {
        return requirements;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(ComponentBundle bundle) {
        return (T) bundle.getComponent(getType());
    }

    public final void setBlueprintType(ComponentType type) {
        try {
            Method setBlueprint = this.getClass().getSuperclass().getSuperclass()
                    .getDeclaredMethod("loom$setBlueprint", ComponentBlueprint.class);
            setBlueprint.setAccessible(true);
            setBlueprint.invoke(this, (this.loader = new LoomBlueprint(type, this)));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public final void update() {
        this.update(GameManager.getGameSeconds());
    }

    /**
     * This method is called about a hundred times a game second.
     *
     * @param delta Float representing a hundredth of a game second.
     *              Used to increase or decrease variables in a countdown.
     */
    public void update(float delta) {}

    public void delete() {}

    public void addEvolutionRequirement(EvolutionRequirement req) {
        this.evolutionRequirements.add(req);
    }

    //TODO
    public String build() {
        return null;
    }

    public static class LoomParams extends ComponentParams {
        private final Map<Field, Object> fields;

        public LoomParams(LoomComponent component) {
            super(component.getType());
            fields = component.registry.hereditaryFields.stream().collect(Collectors.toMap(f -> f, f -> {
                try {
                    f.setAccessible(true);
                    return f.get(component);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        public Map<Field, Object> getFields() {
            return fields;
        }
    }
}

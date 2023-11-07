package loom.component;

import componentArchitecture.*;
import entityInfoGui.PopUpInfoGui;
import equilinox.VanillaLoader;
import equilinox.classification.Classifiable;
import equilinox.classification.Specie;
import javafx.util.Pair;
import loom.entity.weaver.EntityComponent;
import org.lwjgl.util.vector.Vector3f;
import speciesInformation.SpeciesInfoType;
import utils.BinaryReader;
import utils.BinaryWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The component responsible for adding specific behaviors, interactions, and actions to entities.
 * Entities often share common components, such as the {@code Spreader} component, which imparts
 * biome-spreading behavior to plants. However, these components may have varying parameters.
 * In the case of {@code Spreader}, it differentiates the specific biome that the entity spreads.
 *
 * <p>
 * To add a new parameter to the custom component, users must create a constructor.
 * The allowable parameter types include integers, floats, integer arrays, float arrays, {@link Vector3f},
 * booleans, longs, enum values, {@link Classifiable}, {@link Specie},
 * or Strings (excluding semicolons).
 * </p>
 *
 * <p>
 * Additionally, some parameters may be optional. For example, the hiding spot in the {@code Flee} component is
 * null by default. Users must create additional constructors with extra parameters and explicitly register
 * all optionals at {@link #create(ComponentRequester) create} method. The parameters must be registered in order.
 * </p>
 */
public abstract class LoomComponent extends Component implements ComponentReference, ComponentPrint {
    private final List<ComponentPrint> requirements = new ArrayList<>();
    private final List<PopUpInfoGui> info = new ArrayList<>();
    private final List<Action> actions = new ArrayList<>();

    private LoomBlueprint loader = null;

    //TODO SUPPORT CUSTOM TABS
    final Map<SpeciesInfoType, Pair<String, String>> speciesInfo = new HashMap<>();

    final ParamRegistry registry;

    public LoomComponent() {
        super(null);
        this.registry = new ParamRegistry(this);
        register(registry);
    }

    @Override
    public final void getStatusInfo(List<PopUpInfoGui> info) {
        info.addAll(this.info);
    }

    @Override
    public final void getActions(List<Action> actions) {
        actions.addAll(this.actions);
    }

    protected final void addStatusInfo(PopUpInfoGui info) {
        this.info.add(info);
    }

    protected final void addAction(Action action) {
        this.actions.add(action);
    }

    protected final void addSpecieTabInfo(String label, String value) {
        speciesInfo.put(SpeciesInfoType.GENERAL, new Pair<>(label, value));
    }

    protected final void addHabitatTabInfo(String label, String value) {
        speciesInfo.put(SpeciesInfoType.PREFERENCES, new Pair<>(label, value));
    }

    protected final void addAbilitiesTabInfo(String label, String value) {
        speciesInfo.put(SpeciesInfoType.ABILITIES, new Pair<>(label, value));
    }

    @Override
    public void export(BinaryWriter writer) throws IOException {
        for (Field field : registry.dynamicFields) {
            field.setAccessible(true);
            try {
                if (!ParamRegistry.writeBinaryType(writer, field.get(this)))
                    System.err.println("Field " + field.getName() + " is a type supported by the saving engine. " +
                            "Supported types are: integers, floats, longs, shorts, Vector3f's, booleans and strings.");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void create(ComponentBundle bundle) {
        create(new ComponentRequester(requirements, bundle));
    }

    protected abstract void create(ComponentRequester requester);
    protected abstract void register(ParamRegistry registry);

    public Object requestEntityComponentBlueprint(EntityComponent component) {
        return VanillaLoader.getFunctionByClass(component.getClass());
    }

    @Override
    public void load(ComponentBundle bundle, BinaryReader reader) throws Exception {
        create(bundle); for (Field field : registry.dynamicFields) {
            field.set(this, ParamRegistry.readBinaryType(reader, field.getType()));
        }
    }

    public final ComponentLoader getLoader() {
        return loader;
    }

    public List<ComponentPrint> getRequirements() {
        return requirements;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(ComponentBundle bundle) {
        return (T) bundle.getComponent(getType());
    }

    public final void setBlueprintType(ComponentType type) {
        try {
            this.loader = new LoomBlueprint(type, this);
            this.getClass().getMethod("loom$setBlueprint", ComponentBlueprint.class).invoke(this, loader);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {}
}

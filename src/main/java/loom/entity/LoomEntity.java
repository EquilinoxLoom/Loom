package loom.entity;

import com.sun.istack.internal.NotNull;
import food.FoodSectionType;
import loom.component.PrintableComponent;
import loom.entity.life.Death;
import loom.entity.system.Particles;
import loom.entity.weaver.EntityPrint;
import loom.entity.weaver.EntityProcessor;
import loom.equilinox.ducktype.ComponentReference;
import loom.equilinox.ducktype.SoundReference;
import loom.equilinox.vanilla.VanillaComponent;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class LoomEntity implements Entity {
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final Map<PrintableComponent, String> components = new EnumMap(VanillaComponent.class);

    protected final List<ComponentReference> componentReferences = new ArrayList<>();

    EntityProcessor processor;

    final int id;

    protected LoomEntity(int id) {
        this.id = id;
        this.processor = new EntityProcessor(this);
        components.put(VanillaComponent.TRANSFORM, null);
        components.put(VanillaComponent.MESH, null);
    }

    @Override
    public boolean hasComponent(PrintableComponent component) {
        return components.containsKey(component);
    }

    @Override
    public Map<PrintableComponent, String> getComponents() {
        return components;
    }

    @Override
    public final int getId() {
        return id;
    }

    @Override
    public final String getNode() {
        return String.valueOf(id);
    }

    /*
    public void addCustomComponent(LoomComponent component) {
        components.put(component, component.);
        return this;
    }
     */

    public List<ComponentReference> getCustomComponents() {
        return componentReferences;
    }

    //MATERIAL INFORMATION

    private final LinkedHashMap<Color, Integer> materials = new LinkedHashMap<>();

    /**
     * @return a map of prices and colors
     */
    public LinkedHashMap<Color, Integer> getMaterials() {
        return materials;
    }

    public void addColor(int price, Color color) {
        materials.put(color, price);
    }

    public Color getSecondNaturalColor() {
        return null;
    }

    public final String getClassification() {
        return getLineage().getClassification() + getId();
    }

    public final boolean hasComponent(@NotNull VanillaComponent component) {
        return components.containsKey(component);
    }

    public void setRandomSounder(float minCooldown, float maxCooldown, int range, @NotNull SoundReference... sounds) {
        components.put(VanillaComponent.SOUND, "");
        components.put(VanillaComponent.RANDOM_SOUNDER, EntityPrint.print(";", minCooldown, maxCooldown, sounds.length,
                Arrays.stream(sounds).map(SoundReference::id).collect(Collectors.joining(";" + range + ";")), range));
    }

    public void setLoopingSounder(float range, float volume, SoundReference sound) {
        components.put(VanillaComponent.SOUND_LOOPER, ";" + sound.id() + ";;" + range + ";;" + volume);
    }

    protected final Map<FoodSectionType, String> foodInfo = new HashMap<>();

    public Map<FoodSectionType, String> getFoodInfo() {
        return foodInfo;
    }

    public String getEdibleAs(FoodSectionType type) {
        return foodInfo.get(type);
    }

    protected void setEdibleAsWhole(int points) {
        foodInfo.computeIfAbsent(FoodSectionType.WHOLE, type -> "EMBROIDER-" + type.name() + ";" + points + ";" +
                Death.newParticleDeath(new Particles(getMaterials().keySet().iterator().next(), false, 0.1f, 0.8f,
                        Particles.newPoint(), 8, 0.6f, 0.2f, 0.5f, 0.05f, 0, 0, 0, 0.4f, 0.1f, 0.1f)
                        .setDirection(0, 1, 0, 0.3f)).build());
    }

    protected void setEdibleAsWhole(int points, Death death) {
        foodInfo.computeIfAbsent(FoodSectionType.WHOLE, type -> "EMBROIDER-" + type.name() + ";" + points + ";"
                + death.build());
    }

    protected void setEdibleAsSample(int points) {
        foodInfo.computeIfAbsent(FoodSectionType.SAMPLE, type -> "EMBROIDER-" + type.name() + ";" + points);
    }

    protected void setEdibleAsShareable(int points, int portions) {
        foodInfo.computeIfAbsent(FoodSectionType.TO_SHARE, type -> "EMBROIDER-" + type.name() + ";" + points + ";;"
                + portions);
    }

    protected void setEdibleAsHoney(int points) {
        foodInfo.computeIfAbsent(FoodSectionType.HONEY, type -> {
            if (!hasComponent(VanillaComponent.HIVE)) {
                System.err.println("Entity " + id + " must be a structure  container");
                return null;
            }
            return "EMBROIDER-" + type.name() + ";" + points;
        });
    }

    public String build() {
        return processor.build();
    }

    /**
     * @param perchLink Whether the entity dies when the perch slot is removed.
     */
    public void setPerches(boolean perchLink) {
        this.components.put(VanillaComponent.PERCHER, perchLink ? ";1" : ";0");
    }
}

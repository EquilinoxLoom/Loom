package loom.entity;

import com.sun.istack.internal.NotNull;
import equilinox.VanillaComponent;
import equilinox.classification.Specie;
import food.FoodSectionType;
import loom.CustomComponent;
import loom.entity.life.Death;
import loom.entity.other.Particle;
import loom.entity.weaver.EntityProcessor;
import loom.entity.weaver.Printable;

import java.awt.*;
import java.util.List;
import java.util.*;

@SuppressWarnings("unused")
public abstract class LoomEntity implements Specie, Entity {
    protected final Map<VanillaComponent, String> components = new EnumMap<>(VanillaComponent.class);

    protected final List<CustomComponent> customComponents = new ArrayList<>();

    final EntityProcessor processor;

    final int id;

    protected LoomEntity(int id) {
        this.id = id;
        this.processor = new EntityProcessor(this);
        components.put(VanillaComponent.TRANSFORM, null);
        components.put(VanillaComponent.MESH, null);
    }

    @Override
    public Map<VanillaComponent, String> getComponents() {
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

    public Entity addCustomComponent(CustomComponent component) {
        customComponents.add(component);
        return this;
    }

    public List<CustomComponent> getCustomComponents() {
        return customComponents;
    }

    //MATERIAL INFORMATION

    private final Map<Color, Integer> materials = new LinkedHashMap<>();

    /**
     * @return a map of prices and colors
     */
    public Map<Color, Integer> getMaterials() {
        return materials;
    }

    public void addColor(int price, Color color) {
        materials.put(color, price);
    }

    public Color getSecondNaturalColor() {
        return null;
    }

    public String getClassification() {
        return getLineage().getClassification() + getId();
    }

    public final boolean hasComponent(@NotNull VanillaComponent component) {
        return components.containsKey(component);
    }

    /**
     * @param sounds SoundFile.toString() or the name of a Vanilla Sound
     */
    public void setRandomSounder(float minCooldown, float maxCooldown, int range, @NotNull String... sounds) {
        components.put(VanillaComponent.SOUND, "");
        components.put(VanillaComponent.RANDOM_SOUNDER, Printable.print(";", minCooldown, maxCooldown, sounds.length,
                String.join(";" + range + ";", sounds), range));
    }

    public void setLoopingSounder(float range, float volume, String sound) {
        components.put(VanillaComponent.SOUND_LOOPER, ";" + sound + ";;" + range + ";;" + volume);
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
                Death.newParticleDeath(new Particle(getMaterials().keySet().iterator().next(), false, 0.1f, 0.8f,
                        Particle.newPoint(), 8, 0.6f, 0.2f, 0.5f, 0.05f, 0, 0, 0, 0.4f, 0.1f, 0.1f)
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
            /*
            if (!entity.TODO) {
                System.err.println("Entity " + id + " must contain ");
                return null;
            }
            */
            return "EMBROIDER-" + type.name() + ";" + points;
        });
    }

    public String build() {
        processor.buildFooter();
        processor.buildFooter();
        return processor.build();
    }

    /**
     * Whether the entity dies when the perch slot is removed.
     */
    public void setPerches(boolean perchLink) {
        this.components.put(VanillaComponent.PERCHER, perchLink ? ";1" : ";0");
    }
}

package loom.entity.weaver;

import loom.component.LoomComponent;
import loom.component.PrintableComponent;
import loom.entity.Entity;
import loom.entity.system.Colorful;
import loom.entity.system.Particles;
import loom.equilinox.ducktype.SoundReference;
import loom.equilinox.vanilla.VanillaComponent;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Standard implementation of {@link Entity entity} interface, including features common to all equilinox entities
 * like mesh and transform components, being respectively, the managers of the 3D model display in the equilinox world
 * and of the position and size.
 */
@SuppressWarnings("unused")
public abstract class EquilinoxEntity extends Colorful implements Entity {
    /**
     * A map of components and its build values.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final Map<PrintableComponent, String> components = new HashMap<>(new EnumMap(VanillaComponent.class));

    /**
     * The name to be displayed in the game.
     */
    protected final String name;

    /**
     * The id of the entity, automatically generated.
     */
    private final int id;

    protected EquilinoxEntity(String name) {
        this.name = name;

        int identifier = Integer.MIN_VALUE;
        char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            int alpha = c < 91 & c > 64 ? c - 64 : (c < 123 & c > 96 ? c - 96 : 0);
            identifier += (int) Math.pow(10, i) * alpha;
        }
        this.id = identifier;
        getLineage().inherit(this);
        components.put(VanillaComponent.TRANSFORM, null);
        components.put(VanillaComponent.MESH, null);
    }

    @Override
    public final boolean hasComponent(PrintableComponent component) {
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

    public void putCustomComponent(LoomComponent component) {
        components.put(component, component.build());
    }

    public final String getClassification() {
        return getLineage().getClassification() + getId();
    }

    /**
     * @see VanillaComponent#SOUND
     * @see VanillaComponent#RANDOM_SOUNDER
     */
    public void setRandomSounder(float minCooldown, float maxCooldown, int range, @Nonnull SoundReference... sounds) {
        components.put(VanillaComponent.SOUND, "");
        components.put(VanillaComponent.RANDOM_SOUNDER, EntityPrint.print(";", minCooldown, maxCooldown, sounds.length,
                Arrays.stream(sounds).map(SoundReference::id).collect(Collectors.joining(";" + range + ";")), range));
    }

    /**
     * @see VanillaComponent#SOUND_LOOPER
     */
    public void setLoopingSounder(float range, float volume, SoundReference sound) {
        components.put(VanillaComponent.SOUND_LOOPER, ";" + sound.id() + ";;" + range + ";;" + volume);
    }

    public String build() {
        return new EntityProcessor(this).build();
    }

    /**
     * @param perchLink Whether the entity dies when the perch slot is removed.
     * @see VanillaComponent#PERCHER
     */
    public void setPerches(boolean perchLink) {
        this.components.put(VanillaComponent.PERCHER, perchLink ? ";1" : ";0");
    }

    @Override
    public String name() {
        return name;
    }

    /**
     * @see VanillaComponent#PARTICLES
     */
    public void setParticles(Particles particle, float range, int[] stages, boolean usesBaseColor) {
        this.components.put(VanillaComponent.PARTICLES, EntityPrint.print(";;", particle.build(), range,
                stages.length + (stages.length > 0 ? Arrays.stream(stages).boxed().map(String::valueOf)
                        .collect(Collectors.joining(";")) : ""), usesBaseColor));
    }
}

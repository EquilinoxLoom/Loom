package loom.entity.weaver;

import loom.component.PrintableComponent;
import loom.entity.Entity;
import loom.equilinox.ducktype.ComponentReference;
import loom.equilinox.ducktype.SoundReference;
import loom.equilinox.vanilla.VanillaComponent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class LoomEntity implements Entity {
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected final Map<PrintableComponent, String> components = new EnumMap(VanillaComponent.class);

    protected final List<ComponentReference> componentReferences = new ArrayList<>();

    protected final String name;

    EntityProcessor processor;

    private final int id;

    private boolean perch = false;

    protected LoomEntity(String name) {
        this.name = name;

        int identifier = Integer.MIN_VALUE;
        char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            int alpha = c <= 90 & c >= 65 ? c - 64 : (c <= 122 & c >= 97 ? c - 96 : 0);
            identifier += (int) Math.pow(alpha, i + 1);
        }
        this.id = identifier;
        getLineage().inherit(this);
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

    public final boolean hasComponent(@Nonnull VanillaComponent component) {
        return components.containsKey(component);
    }

    public void setRandomSounder(float minCooldown, float maxCooldown, int range, @Nonnull SoundReference... sounds) {
        components.put(VanillaComponent.SOUND, "");
        components.put(VanillaComponent.RANDOM_SOUNDER, EntityPrint.print(";", minCooldown, maxCooldown, sounds.length,
                Arrays.stream(sounds).map(SoundReference::id).collect(Collectors.joining(";" + range + ";")), range));
    }

    public void setLoopingSounder(float range, float volume, SoundReference sound) {
        components.put(VanillaComponent.SOUND_LOOPER, ";" + sound.id() + ";;" + range + ";;" + volume);
    }

    public String build() {
        return new EntityProcessor(this).build();
    }

    public boolean perches() {
        return perch;
    }

    /**
     * @param perchLink Whether the entity dies when the perch slot is removed.
     */
    public void setPerches(boolean perchLink) {
        this.perch = true;
        this.components.put(VanillaComponent.PERCHER, perchLink ? ";1" : ";0");
    }

    @Override
    public String name() {
        return name;
    }
}

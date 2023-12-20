package loom;

import classification.Classification;
import classification.Classifier;
import loom.component.LoomComponent;
import loom.entity.Classifiable;
import loom.entity.Entity;
import loom.entity.Specie;
import loom.equilinox.CustomEatingAnimation;
import loom.equilinox.EvolutionRequirement;
import loom.equilinox.ducktype.BiomeReference;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

import java.lang.reflect.*;
import java.util.*;

public abstract class LoomMod {
    public static final String MOD_POINTER = "@loom@";
    final Map<String, Classifiable> classifications = new HashMap<>();
    final Map<Integer, Entity> entities = new LinkedHashMap<>();

    final List<LoomComponent> components = new ArrayList<>();
    final List<BiomeReference> biomes = new ArrayList<>();
    final List<CustomEatingAnimation> eatingAnimations = new ArrayList<>();

    public static List<LoomMod> LOOMS = new ArrayList<>();

    private final LoomInfo info;

    public LoomMod(String id, String name, String version, String author, String description) {
        this.info = new LoomInfo(id, name, version, author, description);

        if (info.isValid()) LOOMS.add(this);
        else Log.warn(LogCategory.LOG, "Mod information is not valid");
    }

    public void registerComponent(LoomComponent component) {
        components.add(component);
    }

    public void registerBiome(BiomeReference reference) {
        biomes.add(reference);
    }

    public void registerEatingAnimation(CustomEatingAnimation animation) {
        eatingAnimations.add(animation);
    }

    public void registerEntity(Entity entity) {
        entities.put(entity.getId(), entity);
    }

    public void registerEvolutionRequirement(EvolutionRequirement requirement) {
        requirement.getComponent().addEvolutionRequirement(requirement);
    }

    public Classifiable requestNewClassification(Classifiable parent, char id, String name) {
        if (parent instanceof Specie) System.err.println("ERROR registering classification " + name
                + " - parent must be a simple classification");
        if (!Character.isAlphabetic(id)) System.err.println("ERROR registering classification " + name
                + " id must be alphabetic");
        Classifiable classifiable = new Classifiable() {
            @Override
            public Classifiable getLineage() {
                return parent;
            }

            @Override
            public String getNode() {
                return String.valueOf(id);
            }
        };
        classifications.put(name, classifiable);
        try {
            Method createChild = Classification.class.getDeclaredMethod("createChild", char.class, String.class);
            createChild.setAccessible(true);
            createChild.invoke(Classifier.getClassification(parent.getClassification()), id, name);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return classifiable;
    }

    public List<LoomComponent> getComponents() {
        return new ArrayList<>(components);
    }

    public List<BiomeReference> getBiomes() {
        return new ArrayList<>(biomes);
    }

    public List<CustomEatingAnimation> getEatingAnimations() {
        return new ArrayList<>(eatingAnimations);
    }

    public Map<Integer, Entity> getEntities() {
        return new LinkedHashMap<>(entities);
    }

    public LoomInfo getModInfo() {
        return info;
    }
}

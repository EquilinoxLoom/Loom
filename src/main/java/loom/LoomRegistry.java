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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LoomRegistry {
    private final LoomMod mod;

    public LoomRegistry(LoomMod mod) {
        this.mod = mod;
    }

    public void registerComponent(LoomComponent component) {
        mod.components.add(component);
    }

    public void registerBiome(BiomeReference reference) {
        mod.biomes.add(reference);
    }

    public void registerEatingAnimation(CustomEatingAnimation animation) {
        mod.eatingAnimations.add(animation);
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
        mod.classifications.put(name, classifiable);
        try {
            Method createChild = Classification.class.getDeclaredMethod("createChild", char.class, String.class);
            createChild.setAccessible(true);
            createChild.invoke(Classifier.getClassification(parent.getClassification()), id, name);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return classifiable;
    }

    public void registerEntity(Entity entity) {
        mod.entities.put(entity.getId(), entity);
    }

    public void registerEvolutionRequirement(EvolutionRequirement requirement) {
        requirement.getComponent().addEvolutionRequirement(requirement);
    }
}

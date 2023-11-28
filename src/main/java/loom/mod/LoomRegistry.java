package loom.mod;

import classification.Classification;
import classification.Classifier;
import equilinox.classification.Classifiable;
import equilinox.classification.Specie;
import equilinox.ducktype.BiomeReference;
import loom.CustomEatingAnimation;
import loom.component.LoomComponent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LoomRegistry {
    private final LoomMod mod;

    public LoomRegistry(LoomMod mod) {
        this.mod = mod;
    }

    public void registerComponent(LoomComponent component) {
        mod.COMPONENTS.add(component);
    }

    public void registerBiome(BiomeReference reference) {
        mod.BIOMES.add(reference);
    }

    public void registerEatingAnimation(CustomEatingAnimation animation) {
        mod.EATING_ANIMATIONS.add(animation);
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
        mod.CLASSIFICATIONS.put(name, classifiable);
        try {
            Method createChild = Classification.class.getDeclaredMethod("createChild", char.class, String.class);
            createChild.setAccessible(true);
            createChild.invoke(Classifier.getClassification(parent.getClassification()), id, name);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return classifiable;
    }
}

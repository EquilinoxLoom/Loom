package loom.mod;

import classification.Classification;
import classification.Classifier;
import equilinox.classification.Classifiable;
import equilinox.ducktype.BiomeReference;
import equilinoxmodkit.mod.EquilinoxMod;
import equilinoxmodkit.mod.Initializer;
import equilinoxmodkit.mod.PreInitializer;
import loom.CustomEatingAnimation;
import loom.component.LoomComponent;
import loom.entity.Entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LoomMod extends EquilinoxMod {
    public static final String MOD_POINTER = ":loom:";
    static final List<LoomMod> LOOMS = new ArrayList<>();

    final Map<String, Classifiable> CLASSIFICATIONS = new HashMap<>();
    final Map<Integer, Entity> ENTITIES = new HashMap<>();

    final List<LoomComponent> COMPONENTS = new ArrayList<>();
    final List<BiomeReference> BIOMES = new ArrayList<>();
    final List<CustomEatingAnimation> EATING_ANIMATIONS = new ArrayList<>();


    public LoomMod() {
        LoomMod.LOOMS.add(this);
    }

    public static List<LoomMod> getLooms() {
        return new ArrayList<>(LoomMod.LOOMS);
    }

    @Override public final void init(Initializer init) {
        postRegistry();
    }

    @Override public final void preInit(PreInitializer init) {
        registry(new LoomRegistry(this));
        CLASSIFICATIONS.forEach((name, classification) -> {
            try {
                Classification c = Classifier.getClassification(classification.getLineage().getClassification());
                Method createChild = c.getClass().getMethod("createChild", char.class, String.class);
                createChild.setAccessible(true);
                createChild.invoke(c, classification.getNode().charAt(0), name);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
        ENTITIES.forEach((id, entity) -> {

        });
    }

    public abstract void postRegistry();
    public abstract void registry(LoomRegistry registry);

    public List<LoomComponent> getComponents() {
        return new ArrayList<>(COMPONENTS);
    }

    public List<BiomeReference> getBiomes() {
        return new ArrayList<>(BIOMES);
    }

    public List<CustomEatingAnimation> getEatingAnimations() {
        return new ArrayList<>(EATING_ANIMATIONS);
    }

    public Map<Integer, Entity> getEntities() {
        return new HashMap<>(ENTITIES);
    }
}

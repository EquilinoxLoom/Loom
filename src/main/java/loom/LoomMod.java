package loom;

import classification.Classification;
import classification.Classifier;
import equilinoxmodkit.mod.EquilinoxMod;
import equilinoxmodkit.mod.Initializer;
import equilinoxmodkit.mod.PreInitializer;
import loom.component.LoomComponent;
import loom.entity.Classifiable;
import loom.entity.Entity;
import loom.entity.Specie;
import loom.equilinox.CustomEatingAnimation;
import loom.equilinox.EvolutionRequirement;
import loom.equilinox.ducktype.BiomeReference;

import java.lang.reflect.*;
import java.util.*;

public abstract class LoomMod extends EquilinoxMod {
    public static final String MOD_POINTER = "@loom@";
    public static final int BUFFER_SIZE = 40000;

    final Map<String, Classifiable> CLASSIFICATIONS = new HashMap<>();
    final Map<Integer, Entity> ENTITIES = new LinkedHashMap<>();

    final List<LoomComponent> COMPONENTS = new ArrayList<>();
    final List<BiomeReference> BIOMES = new ArrayList<>();
    final List<CustomEatingAnimation> EATING_ANIMATIONS = new ArrayList<>();

    public LoomMod() {}

    @Override public final void init(Initializer init) {}

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
    }

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
        return new LinkedHashMap<>(ENTITIES);
    }

    public static class LoomRegistry {
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

        public void registerEntity(Entity entity) {
            mod.ENTITIES.put(entity.getId(), entity);
        }

        public void registerEvolutionRequirement(EvolutionRequirement requirement) {
            requirement.getComponent().addEvolutionRequirement(requirement);
        }
    }

    public static class ParamRegistry {
        protected final Class<?> clazz;

        /** A map to store the names and types of parameters in the component class. */
        private final LinkedHashMap<String, Class<?>> params = new LinkedHashMap<>();

        /** A map to store the names and types of parameters in the component class. */
        private final LinkedHashMap<String, Class<?>> optionals = new LinkedHashMap<>();

        public ParamRegistry(Class<?> clazz) {
            this.clazz = clazz;

            Constructor<?>[] constructors = clazz.getDeclaredConstructors();

            if (constructors.length == 0) {
                throw new RuntimeException("No constructor found for " + clazz.getName());
            }

            // Sort constructors based on parameter count
            if (constructors.length > 1) {
                Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));
            }

            Constructor<?> previous = constructors[0];

            // Register parameters from the first constructor
            for (Parameter param : previous.getParameters()) {
                params.put(param.getName(), param.getType());
            }

            Map<Integer, Constructor<?>> map = new TreeMap<>();

            for (int i = 1; i < constructors.length; i++) {
                Constructor<?> constructor = constructors[i];
                Constructor<?> old = map.put(constructor.getParameterCount(), constructor);
                if (old != null) System.err.println("Constructor " + old + " was ignored, as it has the same parameter count of " + constructor);
            }

            for (Constructor<?> constructor : map.values()) {
                Parameter[] parameters = constructor.getParameters();
                for (int i = previous.getParameters().length; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    optionals.put(parameter.getName(), parameter.getType());
                }
                previous = constructor;
            }
        }

        public LinkedHashMap<String, Class<?>> getParams() {
            return params;
        }

        public LinkedHashMap<String, Class<?>> getOptionals() {
            return optionals;
        }

        public void addField(List<Field> fields, String name) {
            try {
                fields.add(clazz.getField(name));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("No field with the name " + name + " was found in " + clazz.getName() + e.getMessage());
            }
        }
    }
}

package loom.component;

import blueprints.Blueprint;
import breedingTrees.ReqInfo;
import componentArchitecture.*;
import instances.Entity;
import loom.LoomMod;
import loom.entity.Classifiable;
import loom.entity.Specie;
import loom.entity.weaver.EntityComponent;
import loom.equilinox.EvolutionRequirement;
import loom.equilinox.vanilla.VanillaColor;
import loom.equilinox.vanilla.VanillaLoader;
import org.lwjgl.util.vector.Vector3f;
import speciesInformation.SpeciesInfoLine;
import speciesInformation.SpeciesInfoType;
import utils.CSVReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class LoomBlueprint extends ComponentBlueprint implements ComponentLoader {
    private final LoomComponent component;

    public LoomBlueprint(ComponentType type, LoomComponent component) {
        super(type);
        this.component = component;
        component.requester.traits.forEach((name, trait) -> addTrait(trait));
    }

    @Override
    public ComponentBlueprint load(CSVReader reader, Blueprint blueprint) {
        LoomComponent loom = (LoomComponent) readRegistry(component.registry, reader);
        loom.setBlueprintType(component.getType());
        return component.getBlueprint();
    }

    @Override
    public Requirement loadRequirement(CSVReader reader) {
        Class<? extends EvolutionRequirement> evReq = ((EvolutionRequirement) component.evolutionRequirements.stream()
                .filter(ev -> ev.check(component.requester))
                .toArray()[0]).getClass();
        final EvolutionRequirement req = (EvolutionRequirement) readRegistry(new LoomMod.ParamRegistry(evReq), reader);
        return new Requirement() {
            @Override
            public boolean check(Entity entity) {
                ComponentRequester requester = new ComponentRequester();
                requester.setBundle(new ComponentBundle(entity));
                return req.check(requester);
            }

            @Override
            public void getGuiInfo(List<ReqInfo> list) {
                list.add(new ReqInfo(req.name(), req.value(), VanillaColor.parseColor(req.color())));
            }

            @Override
            public boolean isSecret() {
                return req.isSecret();
            }
        };
    }

    public static Object readRegistry(LoomMod.ParamRegistry registry, CSVReader reader, Constructor<?>... constructors) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        registry.getParams().forEach((label, type) -> {
            reader.getNextString(); params.put(label, readTextType(reader, type));
        });

        registry.getOptionals().forEach((label, type) -> {
            if (reader.isEndOfLine()) {
                reader.getNextString();
                if (reader.isEndOfLine()) params.put(label, readTextType(reader, type));
            }
        });

        for (Constructor<?> constructor : Arrays.stream(constructors)
                .filter(constructor -> constructor.getParameterCount() == params.size()).collect(Collectors.toList())) {
            try {
                constructor.setAccessible(true);
                return constructor.newInstance(params.values().toArray());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {}
        }
        throw new RuntimeException("INTERNAL ERROR: No constructors were found matching " + registry.getParams().values()
                .stream().map(Class::getSimpleName).collect(Collectors.joining(", ",
                        constructors.getClass().getSimpleName() + "(", ") at " + constructors.getClass().getName())),
                new IllegalArgumentException());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> Object readTextType(CSVReader reader, Class<?> clazz) {
        if (clazz.equals(int.class)) return reader.getNextInt();
        else if (clazz.equals(float.class)) return reader.getNextFloat();
        else if (clazz.equals(int[].class)) {
            int count = reader.getNextInt();
            int[] array = new int[count];
            for (int i = 0; i < count; ++i) array[i] = reader.getNextInt();
            return array;
        }
        else if (clazz.equals(float[].class)) {
            int count = reader.getNextInt();
            float[] array = new float[count];
            for (int i = 0; i < count; ++i) array[i] = reader.getNextFloat();
            return array;
        }
        else if (clazz.equals(Vector3f.class)) return reader.getNextVector();
        else if (clazz.equals(boolean.class)) return reader.getNextBool();
        else if (clazz.equals(long.class)) return reader.getNextLong();
        else if (clazz.equals(Classifiable.class)) return reader.getNextString();
        else if (clazz.equals(Specie.class)) return reader.getNextString();
        else if (EntityComponent.class.isAssignableFrom(clazz))
            return VanillaLoader.getFunctionByClass(clazz).apply(reader);
        else if (clazz.isEnum()) return Enum.valueOf((Class<T>) clazz, reader.getNextString());
        else if (clazz.isArray() && clazz.getComponentType() != null && clazz.getComponentType().isEnum()) {
            int count = reader.getNextInt();
            Enum<?>[] array = new Enum[count];
            for (int i = 0; i < count; ++i) array[i] = Enum.valueOf((Class<T>) clazz, reader.getNextString());
            return array;
        }
        else if (clazz.getGenericSuperclass() instanceof ParameterizedType
                && ((ParameterizedType) clazz.getGenericSuperclass()).getRawType() == Map.class) {
            Type[] types = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
            int count = reader.getNextInt();
            Map<Object, Object> map = new HashMap<>();
            for (int i = 0; i < count; ++i) map.put(readTextType(reader, types[0].getClass()), readTextType(reader, types[1].getClass()));
            return map;
        }
        else return reader.getNextString();
    }

    @Override
    public Component createInstance() {
        return component;
    }

    @Override
    public void delete() {
        component.delete();
    }

    @Override
    public void getInfo(Map<SpeciesInfoType, List<SpeciesInfoLine>> map) {
        component.speciesInfo.forEach((type, value) -> {
            String[] line = value.split(LoomMod.MOD_POINTER);
            map.get(type).add(new SpeciesInfoLine(line[0], line[1]));
        });
    }
}

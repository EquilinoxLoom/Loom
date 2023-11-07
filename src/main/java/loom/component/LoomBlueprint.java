package loom.component;

import blueprints.Blueprint;
import componentArchitecture.*;
import equilinox.VanillaLoader;
import equilinox.classification.Classifiable;
import equilinox.classification.Specie;
import loom.entity.weaver.EntityComponent;
import org.lwjgl.util.vector.Vector3f;
import speciesInformation.SpeciesInfoLine;
import speciesInformation.SpeciesInfoType;
import utils.CSVReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoomBlueprint extends ComponentBlueprint implements ComponentLoader {
    private final LoomComponent component;

    public LoomBlueprint(ComponentType type, LoomComponent component) {
        super(type);
        this.component = component;
    }

    @Override
    public ComponentBlueprint load(CSVReader reader, Blueprint blueprint) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();

        component.registry.params.forEach((label, type) -> {
            reader.getNextString(); params.put(label, readTextType(reader, type));
        });
        int i = 0, j = 0;
        for (Map.Entry<Field, Object> entry : component.registry.optionalParams.entrySet()) {
            try {
                i++; if (!entry.getKey().get(component).equals(entry.getValue())) j = i;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        Field[] fields = component.registry.optionalParams.keySet().toArray(new Field[0]);
        for (int l = 0; l < j; l++) if (!reader.isEndOfLine()) {
            reader.getNextString();
            params.put(fields[i].getName(), readTextType(reader, fields[i].getType()));
        }

        List<Constructor<?>> constructors = Arrays.stream(component.getClass().getConstructors())
                .filter(constructor -> constructor.getParameterCount() == params.size()).collect(Collectors.toList());
        if (constructors.isEmpty()) throw new RuntimeException("No constructors were found with " + params.size()
                + " parameters at " + component.getClass().getSimpleName() + ". "
                + component.registry.optionalParams.size() + " optional parameters were registered.");

        for (Constructor<?> constructor : constructors) {
            try {
                constructor.setAccessible(true);
                LoomComponent component = (LoomComponent) constructor.newInstance(params.values().toArray());
                component.setBlueprintType(this.component.getType());
                return component.getBlueprint();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {}
        }
        throw new RuntimeException("No constructors were found matching " + component.registry.params.values()
                .stream().map(Class::getSimpleName).collect(Collectors.joining(", ",
                        component.getClass().getSimpleName() + "(", ") at " + component.getClass().getName())),
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
        else return reader.getNextString();
    }

    @Override
    public Requirement loadRequirement(CSVReader csvReader) {
        return null;
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
        component.speciesInfo.forEach((type, pair) -> map.get(type)
                .add(new SpeciesInfoLine(pair.getKey(), pair.getValue())));
    }
}

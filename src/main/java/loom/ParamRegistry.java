package loom;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;

public class ParamRegistry {
    protected final Class<?> clazz;

    /**
     * A map to store the names and types of parameters in the component class.
     */
    private final LinkedHashMap<String, Class<?>> params = new LinkedHashMap<>();

    /**
     * A map to store the names and types of parameters in the component class.
     */
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
            if (old != null)
                System.err.println("Constructor " + old + " was ignored, as it has the same parameter count of " + constructor);
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

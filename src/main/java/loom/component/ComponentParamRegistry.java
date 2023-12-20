package loom.component;

import loom.ParamRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ComponentParamRegistry extends ParamRegistry {
    /** A list to store dynamic fields that can be saved and loaded dynamically. */
    final List<Field> dynamicFields = new ArrayList<>();

    /** A list to store hereditary fields that are set to the entity's offsprings. */
    final List<Field> hereditaryFields = new ArrayList<>();

    public ComponentParamRegistry(Class<?> clazz) {
        super(clazz);
    }

    /**
     * Registers a dynamic field by adding it to the list of dynamic fields.
     * The field value is updated through reflection every time the game is loaded.
     *
     * @param name The name of the field in the class.
     * @throws RuntimeException If the specified field is not found in the class.
     */
    public void addDynamicField(String name) {
        addField(dynamicFields, name);
    }

    /**
     * Registers a hereditary field by adding it to the list of hereditary fields.
     * The field is set to any offspring of the entity through reflection.
     *
     * @param name The name of the field in the class.
     * @throws RuntimeException If the specified field is not found in the class.
     */
    public void addHereditaryField(String name) {
        addField(hereditaryFields, name);
    }
}

package loom.component;

import org.lwjgl.util.vector.Vector3f;
import utils.BinaryReader;
import utils.BinaryWriter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;

//TODO
/**
 * Used to register custom components, classifications, parameters
 */
public class ParamRegistry {
    private final LoomComponent component;

    /** A map to store the names and types of parameters in the component class. */
    final LinkedHashMap<String, Class<?>> params = new LinkedHashMap<>();

    /** A list to store dynamic fields that can be saved and loaded dynamically. */
    final List<Field> dynamicFields = new ArrayList<>();

    /**
     * @throws RuntimeException If the component does not have a valid constructor.
     */
    public ParamRegistry(LoomComponent component) {
        this.component = component;

        Constructor<?>[] constructors = component.getClass().getDeclaredConstructors();

        if (constructors.length == 0) {
            throw new RuntimeException("No constructor found for " + component.getClass().getName());
        }

        // Sort constructors based on parameter count
        if (constructors.length > 1) {
            Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));
        }

        // Register parameters from the first constructor
        for (Parameter param : constructors[0].getParameters()) {
            params.put(param.getName(), param.getType());
        }
    }

    /**
     * Registers a dynamic field by adding it to the list of dynamic fields.
     * The field value is updated through reflection every time the game is loaded.
     *
     * @param name The name of the field in the class.
     * @throws RuntimeException If the specified field is not found in the class.
     */
    public void addDynamicField(String name) {
        try {
            this.dynamicFields.add(component.getClass().getField(name));
        } catch (NoSuchFieldException e) {
            System.err.println("No field with the name " + name + " was found in " + component.getClass().getName());
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes a binary representation of the specified value using the provided BinaryWriter.
     *
     * @param writer The BinaryWriter to use for writing.
     * @param value  The value to write.
     * @param <T>    The type of the value.
     * @return True if the value was successfully written, false otherwise.
     * @throws IOException if an I/O error occurs.
     * In particular, an {@code IOException} may be thrown if the output stream has been closed.
     */
    public static <T> boolean writeBinaryType(BinaryWriter writer, T value) throws IOException {
        if (value instanceof Integer) writer.writeInt((int) value);
        else if (value instanceof Float) writer.writeFloat((float) value);
        else if (value instanceof Long) writer.writeLong((long) value);
        else if (value instanceof Short) writer.writeShort((short) value);
        else if (value instanceof Vector3f) writer.writeVector((Vector3f) value);
        else if (value instanceof Boolean) writer.writeBoolean((boolean) value);
        else if (value instanceof String) writer.writeString((String) value);
        else return false;
        return true;
    }

    /**
     * Reads a binary representation of a value from the provided BinaryReader, assuming the specified class type.
     *
     * @param reader The BinaryReader to use for reading.
     * @param clazz  The class type of the value.
     * @return The read value.
     * @throws Exception If class is not supported by the reader.
     */
    @SuppressWarnings("unchecked")
    public static <T> T readBinaryType(BinaryReader reader, Class<T> clazz) throws Exception {
        if (clazz.equals(Integer.class)) return (T) Integer.valueOf(reader.readInt());
        else if (clazz.equals(Float.class)) return (T) Float.valueOf(reader.readFloat());
        else if (clazz.equals(Vector3f.class)) return (T) reader.readVector();
        else if (clazz.equals(Boolean.class)) return (T) Boolean.valueOf(reader.readBoolean());
        else if (clazz.equals(Long.class)) return (T) Long.valueOf(reader.readLong());
        else if (clazz.equals(String.class)) return (T) reader.readString();
        else throw new RuntimeException("Unsupported class type");
    }
}

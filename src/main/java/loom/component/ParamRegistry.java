package loom.component;

import org.lwjgl.util.vector.Vector3f;
import utils.BinaryReader;
import utils.BinaryWriter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;

public class ParamRegistry {
    private final LoomComponent component;

    final LinkedHashMap<String, Class<?>> params = new LinkedHashMap<>();
    final LinkedHashMap<Field, Object> optionalParams = new LinkedHashMap<>();
    final List<Field> dynamicFields = new ArrayList<>();

    public ParamRegistry(LoomComponent component) {
        this.component = component;

        Constructor<?>[] constructors = component.getClass().getDeclaredConstructors();

        if (constructors.length == 0) throw new RuntimeException();
        if (constructors.length > 1) Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));
        for (Parameter param : constructors[0].getParameters()) params.put(param.getName(), param.getType());
    }

    public <T> void registerOptionalParam(String fieldName, T baseValue) {
        try {
            optionalParams.computeIfAbsent(component.getClass().getField(fieldName), a -> baseValue);
        } catch (NoSuchFieldException e) {
            System.err.println("No field with the name " + fieldName + " was found in " + component.name());
            throw new RuntimeException(e);
        }
    }

    public void addDynamicField(String fieldName) {
        try {
            this.dynamicFields.add(component.getClass().getField(fieldName));
        } catch (NoSuchFieldException e) {
            System.err.println("No field with the name " + fieldName + " was found in " + component.getClass().getName());
            throw new RuntimeException(e);
        }
    }

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

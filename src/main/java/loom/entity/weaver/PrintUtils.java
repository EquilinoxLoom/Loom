package loom.entity.weaver;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for formatting and printing game data.
 * Game data is stored in the game files in a special format where data is split by semicolons and line breaks.
 * Booleans are stored as 0s and 1s, arrays are stored as "array length;item1;item2;item3;...", and
 * vectors stored as 3 floating points split by semicolons. The rest stored as its string value.
 */
public class PrintUtils {
    private PrintUtils() {}

    /**
     * Joins the given objects with the specified joiner. Booleans are converted to 0s and 1s.
     *
     * @param joiner The joiner to use for concatenation.
     * @param os     The objects to print.
     * @return The formatted string.
     */
    public static String print(String joiner, Object... os) {
        return process(joiner, o -> (o instanceof Boolean ? ((boolean) o) ? 1 : 0 : o), os);
    }

    /**
     * Joins the given objects with the specified joiner, applying the provided function to each object.
     *
     * @param joiner The joiner to use for concatenation.
     * @param func   The function to apply to each object.
     * @param os     The objects to process and print.
     * @return The formatted string.
     */
    public static String process(String joiner, Function<Object, Object> func, Object... os) {
        return Arrays.stream(os)
                .filter(Objects::nonNull)
                .map(func).map(String::valueOf)
                .collect(Collectors.joining(joiner));
    }

    /**
     * Joins an array of objects with the specified joiner inserting its length at the start of the string.
     *
     * @param joiner The joiner to use for concatenation.
     * @param os     The array of objects to print.
     * @param func   The function to apply to each object in the array.
     * @param <T>    The type of the array elements.
     * @return The formatted string.
     */
    public static <T> String printArray(String joiner, T[] os, Function<T, String> func) {
        return os.length + joiner + Arrays.stream(os).map(func).collect(Collectors.joining(joiner));
    }

    /**
     * Joins an array of integers with the specified joiner inserting its length at the start of the string.
     *
     * @param joiner The joiner to use for concatenation.
     * @param is     The array of integers to print.
     * @param func   The function to apply to each object in the array.
     * @return The formatted string.
     */
    public static String printArray(String joiner, int[] is, Function<Integer, String> func) {
        return is.length + joiner + Arrays.stream(is).boxed().map(func).collect(Collectors.joining(joiner));
    }
}
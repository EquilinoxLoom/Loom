package loom.entity.weaver;

import com.sun.istack.internal.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Printable {
    String build();

    @NotNull
    default String joiner() {
        return ";;";
    }

    default String joined(Object... os) {
        return print(joiner(), os);
    }

    static String print(String joiner, Object... os) {
        return Arrays.stream(os).filter(Objects::nonNull).map(o -> {
            if (o instanceof Boolean) return ((boolean) o) ? "1" : "0";
            return String.valueOf(o);
        }).collect(Collectors.joining(joiner));
    }

    static <T> String printArray(String joiner, T[] os, Function<T, String> func) {
        return os.length + joiner + Arrays.stream(os).map(func).collect(Collectors.joining(joiner));
    }

    static String printArray(String joiner, int[] is, Function<Integer, String> func) {
        return is.length + joiner + Arrays.stream(is).boxed().map(func).collect(Collectors.joining(joiner));
    }
}
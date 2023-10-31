package loom.entity.weaver;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Printable {
    String build();

    default String joiner() {
        return ";;";
    }

    default String join(Object... os) {
        return Arrays.stream(os).filter(Objects::nonNull).map(o -> {
            if (o instanceof Boolean) return ((boolean) o) ? "1" : "0";
            return String.valueOf(o);
        }).collect(Collectors.joining(joiner()));
    }

    static String print(String joiner, Object... os) {
        return process(joiner, o -> (o instanceof Boolean ? ((boolean) o) ? 1 : 0 : o), os);
    }

    static String process(String joiner, Function<Object, Object> func, Object... os) {
        return Arrays.stream(os).filter(Objects::nonNull).map(func).map(String::valueOf)
                .collect(Collectors.joining(joiner));
    }

    static <T> String printArray(String joiner, T[] os, Function<T, String> func) {
        return os.length + joiner + Arrays.stream(os).map(func).collect(Collectors.joining(joiner));
    }

    static String printArray(String joiner, int[] is, Function<Integer, String> func) {
        return is.length + joiner + Arrays.stream(is).boxed().map(func).collect(Collectors.joining(joiner));
    }
}
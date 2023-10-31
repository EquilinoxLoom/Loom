package loom.entity.other;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Entity fades after specified times
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout {
    float value();
}


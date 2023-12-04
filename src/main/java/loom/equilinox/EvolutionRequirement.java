package loom.equilinox;

import loom.component.ComponentRequester;
import loom.component.LoomComponent;
import loom.entity.Classifiable;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;

/**
 * A requirement for an entity to be evolved from its parent entity. Entities often share common evolution requirements,
 * such as the {@code Altitude} requirement, which implies that the entity must be in a specified height from sea level
 * to evolve. However, the height varies from entity to entity.
 *
 * <p>
 * To add a new parameter to the custom evolution requirement, users must create a constructor.
 * The allowable parameter types are: integers, floats, integer arrays, float arrays, {@link Vector3f}s,
 * booleans, longs, enums, {@link Classifiable}s, strings (excluding semicolons),
 * or also maps of allowable parameters as both key and value.
 * </p>
 */
public interface EvolutionRequirement {
    boolean check(ComponentRequester requester);

    String name();

    String value();

    default Color color() {
        return new Color(237, 224, 178);
    }

    default boolean isSecret() {
        return false;
    }

    LoomComponent getComponent();
}


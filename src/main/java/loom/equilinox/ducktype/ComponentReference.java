package loom.equilinox.ducktype;

import componentArchitecture.Component;
import componentArchitecture.ComponentLoader;
import componentArchitecture.ComponentType;
import loom.component.LoomBlueprint;

/**
 * Used to duck type {@link ComponentType}, implementing its methods.
 */
public interface ComponentReference {

    /**
     * Retrieves the component loader responsible for loading parameters from the game file.
     * The component loaders are the first class to be instantiated by the game.
     *
     * @return The component loader.
     * @see LoomBlueprint
     * @see loom.entity.weaver.EntityProcessor EntityProcessor
     */
    ComponentLoader getLoader();

    /**
     * @return The name of the component reference.
     */
    String name();

    /**
     * Indicates whether the component is dynamic and depends on dynamic variables to function.
     * Examples include movement, animation, flee components that use the entity's position,
     * or sleep component that relies on the day time.
     *
     * @return True if the component is dynamic, false otherwise.
     */
    boolean isDynamic();

    /**
     * Indicates whether the component is active and should be {@link Component#update updated}
     * every game tick.
     *
     * @return True if the component is active, false otherwise.
     */
    boolean isActive();
}

package loom.component;

import componentArchitecture.Component;
import componentArchitecture.ComponentBundle;
import equilinox.VanillaComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to retrieve component information for an entity.
 */
public class ComponentRequester {
    private final ComponentBundle bundle;
    final List<PrintableComponent> requirements = new ArrayList<>();

    public ComponentRequester(ComponentBundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Requests and retrieves the Equilinox component represented by the given vanilla component
     * obtained from the entity bundle.
     *
     * @param component The vanilla component to request.
     * @param <T>       The type of the requested component.
     * @return The Equilinox component represented by the vanilla component, or null if not present in the bundle.
     * @see VanillaComponent#getComponent(ComponentBundle)
     */
    public <T extends Component> T requestComponent(VanillaComponent component) {
        this.requirements.add(component);
        return component.getComponent(bundle);
    }

    /**
     * Requests and retrieves the Equilinox component represented by the given custom component
     * obtained from the entity bundle.
     *
     * @param component The custom component to request.
     * @param <T>       The type requested component.
     * @return The Equilinox component represented by the custom component, or null if not present in the bundle.
     * @see LoomComponent#getComponent(ComponentBundle)
     */
    public <T extends LoomComponent> T requestComponent(T component) {
        this.requirements.add(component);
        return component.getComponent(bundle);
    }
}

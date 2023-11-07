package loom.component;

import componentArchitecture.Component;
import componentArchitecture.ComponentBundle;
import equilinox.VanillaComponent;

import java.util.List;

public class ComponentRequester {
    private final List<ComponentPrint> requirements;
    private final ComponentBundle bundle;

    public ComponentRequester(List<ComponentPrint> requirements, ComponentBundle bundle) {
        this.requirements = requirements;
        this.bundle = bundle;
    }

    public <T extends Component> T requestComponent(VanillaComponent component) {
        this.requirements.add(component);
        return component.getComponent(bundle);
    }

    public <T extends LoomComponent> T requestComponent(T component) {
        this.requirements.add(component);
        return component.getComponent(bundle);
    }
}

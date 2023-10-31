package loom.component;

import componentArchitecture.ComponentLoader;
import componentArchitecture.ComponentType;

public abstract class LoomComponent {
    public final String name;
    public final boolean dynamic, active;

    public ComponentType type = null;

    public LoomComponent(String name, boolean dynamic, boolean active) {
        this.name = name;
        this.dynamic = dynamic;
        this.active = active;
    }

    public abstract ComponentLoader getLoader();

    public void setType(ComponentType type) {
        if (this.type == null) this.type = type;
    }
}

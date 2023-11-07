package loom.component;

import componentArchitecture.ComponentLoader;
import componentArchitecture.ComponentType;

public interface ComponentReference {
    ComponentLoader getLoader();

    String name();

    boolean isDynamic();
    boolean isActive();

    void setBlueprintType(ComponentType type);
}

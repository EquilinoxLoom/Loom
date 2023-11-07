package loom.mod;

import loom.component.ComponentReference;

public class LoomRegistry {
    private final LoomMod mod;

    public LoomRegistry(LoomMod mod) {
        this.mod = mod;
    }

    public void registerComponent(ComponentReference component) {
        mod.COMPONENTS.add(component);
    }
}

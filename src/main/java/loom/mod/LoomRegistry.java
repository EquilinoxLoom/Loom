package loom.mod;

import loom.component.LoomComponent;

public class LoomRegistry {
    private final LoomMod mod;

    public LoomRegistry(LoomMod mod) {
        this.mod = mod;
    }

    public void registerComponent(LoomComponent component) {
        mod.COMPONENTS.add(component);
    }
}

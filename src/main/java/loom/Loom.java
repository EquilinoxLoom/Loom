package loom;

import equilinoxmodkit.mod.ModInfo;
import loom.mod.LoomMod;
import loom.mod.LoomRegistry;

@ModInfo(
        id = "equilinox.loom",
        name = "Loom",
        version = "0.0.2",
        author = "Sandáliaball",
        description = "This is a an open source modding tool for entities.",
        thumbnail = "example_mods.png"
)
public class Loom extends LoomMod {
    @Override
    public void postRegistry() {}

    @Override
    public void registry(LoomRegistry registry) {}
}

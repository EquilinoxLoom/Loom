package loom;

import equilinox.classification.Family;
import equilinoxmodkit.mod.ModInfo;
import equilinoxmodkit.mod.PreInitializer;
import loom.mod.LoomMod;
import loom.mod.LoomRegistry;
import org.spongepowered.asm.mixin.Mixins;

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
    public void preInit(PreInitializer pInit) {
        Mixins.addConfiguration("mixins.loom.json");
    }

    @Override
    public void postRegistry() {
    }

    @Override
    public void registry(LoomRegistry registry) {}
}

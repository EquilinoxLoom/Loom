package loom;

import equilinoxmodkit.mod.EquilinoxMod;
import equilinoxmodkit.mod.Initializer;
import equilinoxmodkit.mod.ModInfo;
import equilinoxmodkit.mod.PreInitializer;

@ModInfo(
        id = "loom",
        name = "Loom",
        version = "0.0.1",
        author = "Sandáliaball",
        description = "This is a an open source modding tool for entities.",
        thumbnail = "example_mods.png"
)
public class Loom extends EquilinoxMod {
    public static final String MOD_POINTER = ":equilinox-mod:";

    @Override
    public void preInit(PreInitializer pInit) {
    }

    @Override
    public void init(Initializer init) {
    }
}

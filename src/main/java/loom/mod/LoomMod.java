package loom.mod;

import equilinoxmodkit.mod.EquilinoxMod;
import equilinoxmodkit.mod.Initializer;
import loom.component.ComponentReference;

import java.util.ArrayList;
import java.util.List;

public abstract class LoomMod extends EquilinoxMod {
    public static final String MOD_POINTER = ":loom:";

    static final List<LoomMod> LOOMS = new ArrayList<>();

    final List<ComponentReference> COMPONENTS = new ArrayList<>();

    public LoomMod() {
        LoomMod.LOOMS.add(this);
    }

    public static List<LoomMod> getLooms() {
        return new ArrayList<>(LoomMod.LOOMS);
    }

    @Override public final void init(Initializer init) {}

    public abstract void postRegistry();
    public abstract void registry(LoomRegistry registry);

    public List<ComponentReference> getComponents() {
        return new ArrayList<>(COMPONENTS);
    }
}

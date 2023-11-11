package loom.mod;

import equilinox.classification.Classifiable;
import equilinoxmodkit.mod.EquilinoxMod;
import equilinoxmodkit.mod.Initializer;
import loom.component.ComponentReference;
import loom.component.LoomComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LoomMod extends EquilinoxMod {
    public static final String MOD_POINTER = ":loom:";
    static final List<LoomMod> LOOMS = new ArrayList<>();

    final Map<String, Classifiable> CLASSIFICATIONS = new HashMap<>();
    final List<LoomComponent> COMPONENTS = new ArrayList<>();

    public LoomMod() {
        LoomMod.LOOMS.add(this);
    }

    public static List<LoomMod> getLooms() {
        return new ArrayList<>(LoomMod.LOOMS);
    }

    @Override public final void init(Initializer init) {}

    public abstract void postRegistry();
    public abstract void registry(LoomRegistry registry);

    public Map<String, Classifiable> getClassifications() {
        return new HashMap<>(CLASSIFICATIONS);
    }

    public List<LoomComponent> getComponents() {
        return new ArrayList<>(COMPONENTS);
    }
}

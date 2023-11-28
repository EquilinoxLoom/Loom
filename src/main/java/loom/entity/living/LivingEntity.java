package loom.entity.living;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import loom.entity.LoomEntity;

public abstract class LivingEntity extends LoomEntity implements Tooltip, Living {
    private final String name;

    private Death death;
    private int defense;

    public LivingEntity(String name, int id, @NotNull Death death) {
        super(id);
        this.name = name;
        this.death = death;
    }

    @Override
    public String name() {
        return name;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public Death getDeath() {
        return death;
    }

    public void setDeath(Death death) {
        this.death = death;
    }

    @Nullable public abstract Evolution getEvolution();
    @NotNull public abstract WellBeing getWellBeing();
}

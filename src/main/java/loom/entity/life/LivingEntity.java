package loom.entity.life;

import com.sun.istack.internal.NotNull;
import loom.entity.LoomEntity;
import loom.entity.Tooltip;

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
}

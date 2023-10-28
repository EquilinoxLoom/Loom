package loom.entity.life;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import loom.entity.LoomEntity;
import snowball.embroider.entity.life.*;

public abstract class LivingEntity extends LoomEntity implements Tooltip, Living {
    public final Evolution evolution;
    public final WellBeing satisfaction;

    private final String name;

    private Death death;
    private int defense;

    public LivingEntity(String name, int id,
                        @NotNull Death death,
                        @Nullable Evolution evolution,
                        @NotNull WellBeing satisfaction) {
        super(id);
        this.name = name;
        this.death = death;
        this.evolution = evolution;
        this.satisfaction = satisfaction;
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

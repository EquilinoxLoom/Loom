package loom.entity.life;

import loom.entity.Specie;
import loom.entity.Tooltip;
import loom.entity.weaver.EquilinoxEntity;
import loom.equilinox.vanilla.VanillaComponent;

import javax.annotation.Nonnull;

public abstract class LivingEntity extends EquilinoxEntity implements Tooltip, Living {
    private Death death;
    private int defense;

    public LivingEntity(@Nonnull String name, @Nonnull Death death) {
        super(name);
        this.death = death;
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

    public void setDrops(Specie item) {
        components.put(VanillaComponent.DROP, ";" + item.getId());
    }
}

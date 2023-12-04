package loom.entity.food;

import loom.entity.LoomEntity;
import loom.entity.Tooltip;
import loom.entity.system.Particles;
import loom.equilinox.vanilla.VanillaComponent;

public abstract class FoodEntity extends LoomEntity {
    protected FoodEntity(int id, float decayingTime) {
        super(id);
        //Meats implement Tooltip
        this.components.put(this instanceof Tooltip ? VanillaComponent.ITEM : VanillaComponent.DECAY,
                String.valueOf(decayingTime));
    }

    /**
     * Particles spawned when it's eaten
     */
    void setDiseaseHealer(Particles particle) {
        this.components.put(VanillaComponent.HEALER, ";PARTICLE_DEATH;;" + particle.toString());
    }
}

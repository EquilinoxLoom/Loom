package loom.entity;

import equilinox.vanilla.VanillaComponent;
import loom.entity.living.Tooltip;
import loom.entity.other.Particle;

public abstract class FoodEntity extends LoomEntity {
    protected FoodEntity(int id, float decayingTime) {
        super(id);
        //Meats implement Tooltip
        this.components.put(this instanceof Tooltip ? VanillaComponent.ITEM : VanillaComponent.DECAY,
                String.valueOf(decayingTime));
    }

    /**
     * Particle spawned when it's eaten
     */
    void setDiseaseHealer(Particle particle) {
        this.components.put(VanillaComponent.HEALER, ";PARTICLE_DEATH;;" + particle.toString());
    }
}

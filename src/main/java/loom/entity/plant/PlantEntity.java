package loom.entity.plant;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import loom.entity.life.LivingEntity;
import loom.entity.life.Death;
import loom.entity.life.Evolution;
import loom.entity.life.WellBeing;
import food.FoodSectionType;
import loom.entity.other.Particle;
import loom.entity.weaver.Printable;
import snowball.embroider.entity.life.*;
import equilinox.VanillaComponent;

import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class PlantEntity extends LivingEntity {
    public PlantEntity(String name, int id, @Nullable Evolution evolution, @NotNull WellBeing satisfaction) {
        super(name, id, Death.newFadeDeath(0.5f), evolution, satisfaction);
    }

    public void setParticles(Particle particle, float range, int[] stages, boolean usesPlantColor) {
        this.components.put(VanillaComponent.PARTICLES, Printable.print(";;", particle.build(), range,
                stages.length + (stages.length > 0 ? Arrays.stream(stages).boxed().map(String::valueOf)
                        .collect(Collectors.joining(";")) : ""), usesPlantColor));
    }

    protected void addEatenFruit(int points) {
        //TODO Fruit Producer
        foodInfo.computeIfAbsent(FoodSectionType.FRUIT, type -> "EMBROIDER-" + type.name() + ";" + points);
    }

    public final boolean dynamic() {
        return false;
    }

    public abstract int subStages();

    public void setBlooms() {
        this.components.put(VanillaComponent.BLOOM, "");
    }
}

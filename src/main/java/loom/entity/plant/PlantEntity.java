package loom.entity.plant;

import food.FoodSectionType;
import loom.entity.life.Death;
import loom.entity.life.LivingEntity;
import loom.entity.system.Particles;
import loom.entity.weaver.EntityPrint;
import loom.equilinox.vanilla.VanillaComponent;
import org.lwjgl.util.vector.Vector3f;

import java.util.Arrays;
import java.util.stream.Collectors;

import static loom.equilinox.vanilla.VanillaComponent.PERCH;

@SuppressWarnings("unused")
public abstract class PlantEntity extends LivingEntity {
    public PlantEntity(String name, int id) {
        super(name, id, Death.newFadeDeath(0.5f));
    }

    public void setParticles(Particles particle, float range, int[] stages, boolean usesPlantColor) {
        this.components.put(VanillaComponent.PARTICLES, EntityPrint.print(";;", particle.build(), range,
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

    public void setPerchingPositions(Vector3f[] positions) {
        if (positions != null && positions.length > 0) {
            StringBuilder builder = new StringBuilder(";" + positions.length + ";");
            for (Vector3f vector : positions) builder.append(";").append(vector.x).append(";").append(vector.y)
                    .append(";").append(vector.z);
            components.put(PERCH, builder.toString());
        }
    }
}

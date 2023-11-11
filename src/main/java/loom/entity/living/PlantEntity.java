package loom.entity.living;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import equilinox.VanillaComponent;
import food.FoodSectionType;
import loom.entity.other.Particle;
import loom.entity.weaver.PrintUtils;
import org.lwjgl.util.vector.Vector3f;

import java.util.Arrays;
import java.util.stream.Collectors;

import static equilinox.VanillaComponent.PERCH;

@SuppressWarnings("unused")
public abstract class PlantEntity extends LivingEntity {
    public PlantEntity(String name, int id, @Nullable Evolution evolution, @NotNull WellBeing satisfaction) {
        super(name, id, Death.newFadeDeath(0.5f), evolution, satisfaction);
    }

    public void setParticles(Particle particle, float range, int[] stages, boolean usesPlantColor) {
        this.components.put(VanillaComponent.PARTICLES, PrintUtils.print(";;", particle.build(), range,
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

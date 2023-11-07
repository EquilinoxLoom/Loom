package loom.entity.life;

import com.sun.istack.internal.NotNull;
import equilinox.classification.Classifiable;
import equilinox.classification.Specie;
import loom.Util;
import loom.entity.weaver.EntityComponent;

import java.awt.*;

@SuppressWarnings("unused")
public class Evolution extends EntityComponent {
    public final int parent, points;

    public Evolution(Specie parent, int breedPoints) {
        this.parent = parent.getId();
        this.points = breedPoints;
    }

    public void addEatingRequirement(@NotNull Classifiable classification) {
        add("EATING;" + classification);
    }

    public void addFruitProductivityRequirement(float target) {
        add("FRUIT_FALL;" + target);
    }

    private void addEnvironmentRequirement(int id, Object... args) {
        add("LIFE", 2, id, args);
    }

    public void addDiseaseRequirement() {
        add("LIFE", 1);
    }

    public void addNearbySpecieRequirement(@NotNull Classifiable classification, int count) {
        addEnvironmentRequirement(0, classification.getClassification(), count);
    }

    /**
     * The biomes are Grassland, Forest, River Bed, Desert, Snow, Jungle, Swamp, Lush, Woodland and Tropical
     */
    public void addBiomeRequirement(@NotNull String biome, float targetPercentage) {
        addEnvironmentRequirement(1, biome.toUpperCase(), targetPercentage);
    }

    public void addSatisfactionRequirement(float satisfaction) {
        addEnvironmentRequirement(2, satisfaction);
    }

    public void addAltitudeRequirement(int min, int max) {
        addEnvironmentRequirement(3, min, max);
    }

    /**
     * Specified biome must be lower than 8% at the environment.
     */
    public void addNotBiomeRequirement(@NotNull String biome) {
        addEnvironmentRequirement(4, biome, "");
    }

    public void addNearbyColoredSpecieRequirement(@NotNull Classifiable classification, Color color) {
        addEnvironmentRequirement(6, classification.getClassification(), Util.getClosestVanilla(color));
    }

    public void addColorRequirement(Color color) {
        add("MATERIAL", Util.getClosestVanilla(color));
    }

    public void addSpeedRequirement(float targetSpeed) {
        add("MOVEMENT;" + targetSpeed);
    }

    public void addSizeRequirement(float targetSize) {
        add("TRANSFORM;" + targetSize);
    }
}

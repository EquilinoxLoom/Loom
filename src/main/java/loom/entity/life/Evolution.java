package loom.entity.life;

import com.sun.istack.internal.NotNull;
import loom.entity.Classifiable;
import loom.entity.Specie;
import loom.entity.weaver.EntityComponent;
import loom.equilinox.ducktype.BiomeReference;
import loom.equilinox.vanilla.VanillaColor;

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

    public void addBiomeRequirement(@NotNull BiomeReference biome, float targetPercentage) {
        addEnvironmentRequirement(1, biome.name().toUpperCase(), targetPercentage);
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
    public void addNoBiomeRequirement(@NotNull BiomeReference biome) {
        addEnvironmentRequirement(4, biome, "");
    }

    public void addNearbyColoredSpecieRequirement(@NotNull Classifiable classification, Color color) {
        addEnvironmentRequirement(6, classification.getClassification(), VanillaColor.getClosestVanilla(color));
    }

    public void addColorRequirement(Color color) {
        add("MATERIAL", VanillaColor.getClosestVanilla(color));
    }

    public void addSpeedRequirement(float targetSpeed) {
        add("MOVEMENT;;" + targetSpeed);
    }

    public void addSizeRequirement(float targetSize) {
        add("TRANSFORM;;" + targetSize);
    }

    /*
    public <T extends LoomComponent & ComponentEvolutionRequirement> void addCustomRequirement(T requirement) {
        add(requirement.name() + ";" + requirement.evolutionRequirement().compile());
    }
     */
}

package loom.entity.structure;

import loom.entity.Tooltip;
import loom.entity.weaver.LoomEntity;
import loom.equilinox.vanilla.VanillaComponent;

public abstract class StructureEntity extends LoomEntity implements Tooltip {
    private final int stages;

    protected StructureEntity(String name, int buildStages, int maxBuildPoints) {
        super(name);
        this.components.put(VanillaComponent.BUILD, buildStages + ";" + maxBuildPoints);
        this.stages = buildStages;
    }

    protected StructureEntity(String name, int buildStages, int maxBuildPoints, int fullyBuiltPoints) {
        super(name);
        this.components.put(VanillaComponent.BUILD, buildStages + ";" + maxBuildPoints + ";;" + fullyBuiltPoints);
        this.stages = buildStages;
    }

    public void setDecays(float timePerStageLoss) {
        this.components.put(VanillaComponent.DECOMPOSE, ";" + timePerStageLoss);
    }

    /**
     * Hives are containers of honey, after fully build, they have 4 honey model stages that are filled by bees.
     */
    public void setContainer(int maxPoints, int containerStages) {
        this.components.put(VanillaComponent.HIVE, maxPoints + ";" + stages + ";" + containerStages);
    }
}

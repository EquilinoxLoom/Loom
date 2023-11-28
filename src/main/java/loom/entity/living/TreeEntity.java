package loom.entity.living;

import equilinox.classification.Specie;
import loom.entity.weaver.PrintUtils;

import java.awt.*;

import static equilinox.vanilla.VanillaComponent.FRUIT_FALL;
import static equilinox.vanilla.VanillaComponent.WOOD;

public abstract class TreeEntity extends PlantEntity {
    public TreeEntity(String name, int id, Evolution evolution, WellBeing satisfaction) {
        super(name, id, evolution, satisfaction);
    }

    /**
     * @param cuttingTime time in game seconds {@code default: 4}
     * @param barkChance chance of spawning bark (beaver food) {@code default: 0.4}
     */
    public TreeEntity(String name, int id, Evolution evolution, WellBeing satisfaction, float cuttingTime,
                      float barkChance, Color woodColour) {
        super(name, id, evolution, satisfaction);
        this.components.put(WOOD, ";" + cuttingTime + ";;" + barkChance + ";;" +
                woodColour.getRed() + ";" + woodColour.getGreen() + ";" + woodColour.getBlue());
    }

    public void setFruiter(Specie fruit, float fallChancePerHour, float height, float radius) {
        components.put(FRUIT_FALL, ";" + PrintUtils.print(";;", fruit.getId(), 0.012f / fallChancePerHour, height, radius));
    }
}

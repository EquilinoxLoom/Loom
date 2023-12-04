package loom.entity.plant;

import loom.entity.Specie;
import loom.entity.weaver.EntityPrint;

import java.awt.*;

import static loom.equilinox.vanilla.VanillaComponent.FRUIT_FALL;
import static loom.equilinox.vanilla.VanillaComponent.WOOD;

public abstract class TreeEntity extends PlantEntity {
    public TreeEntity(String name, int id) {
        super(name, id);
    }

    /**
     * @param cuttingTime time in game seconds {@code default: 4}
     * @param barkChance chance of spawning bark (beaver food) {@code default: 0.4}
     */
    public TreeEntity(String name, int id, float cuttingTime, float barkChance, Color woodColour) {
        super(name, id);
        this.components.put(WOOD, ";" + cuttingTime + ";;" + barkChance + ";;" +
                woodColour.getRed() + ";" + woodColour.getGreen() + ";" + woodColour.getBlue());
    }

    public void setFruiter(Specie fruit, float fallChancePerHour, float height, float radius) {
        components.put(FRUIT_FALL, ";" + EntityPrint.print(";;", fruit.getId(), 0.012f / fallChancePerHour, height, radius));
    }
}

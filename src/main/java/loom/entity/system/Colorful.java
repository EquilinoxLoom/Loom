package loom.entity.system;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.SequencedSet;

public class Colorful {
    private final LinkedHashMap<Color, Integer> colors = new LinkedHashMap<>();
    private boolean second = false;

    public LinkedHashMap<Color, Integer> getMaterials() {
        return colors;
    }

    public void addColor(int price, Color color) {
        colors.put(color, price);
    }

    public Color getSecondNaturalColor() {
        if (second) {
            SequencedSet<Color> materials = colors.sequencedKeySet();
            materials.removeFirst();
            return materials.getFirst();
        } else {
            return null;
        }
    }

    public void setHasSecondNaturalColor() {
        this.second = true;
    }
}

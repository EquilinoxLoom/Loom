package equilinox.vanilla;

import equilinox.ducktype.BiomeReference;

import java.awt.*;
import java.util.Locale;

public enum VanillaBiome implements BiomeReference {
    GRASSLAND(new Color(131, 185, 98)),
    FOREST(new Color(160, 176, 47)),
    RIVER_BED(new Color(217, 191, 113)),
    DESERT(new Color(232, 224, 149)),
    SNOW(new Color(240, 240, 245)),
    JUNGLE(new Color(86, 125, 80)),
    SWAMP(new Color(115, 100, 99)),
    LUSH(new Color(203, 165, 181)),
    WOODLAND(new Color(116, 184, 119)),
    TROPICAL(new Color(188, 204, 129));

    private final String name;
    private final Color color;

    VanillaBiome(Color color) {
        this.name = name().charAt(0) + name().substring(1).toLowerCase(Locale.ROOT);
        this.color = color;
    }

    @Override
    public String inGameName() {
        return name;
    }

    @Override
    public Color color() {
        return color;
    }
}

package equilinox;

import loom.IEnvironment;

public enum Environment implements IEnvironment {
    GRASSLAND, FOREST, RIVER_BED, DESERT, SNOW, JUNGLE, SWAMP, LUSH, WOODLAND, TROPICAL;

    @Override
    public int getId() {
        return ordinal();
    }
}

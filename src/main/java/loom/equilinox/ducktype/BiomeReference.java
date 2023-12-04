package loom.equilinox.ducktype;

import biomes.Biome;
import loom.entity.system.Particles;

import java.awt.*;

/**
 * Used to duck type {@link Biome}, implementing its methods.
 */
public interface BiomeReference {
    /**
     * @return the internal biome name.
     */
    String name();

    /**
     * @return the biome true name.
     */
    String inGameName();

    /**
     * @return the color of the ground of the biome.
     */
    Color color();

    /**
     * @return the particles spawn by the biome.
     */
    default Particles particle() {
        return null;
    }

    /**
     * @return the sound randomly played by the biome.
     */
    default SoundReference sound() {
        return null;
    }
}

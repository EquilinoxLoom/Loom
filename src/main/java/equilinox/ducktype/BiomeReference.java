package equilinox.ducktype;

import biomes.Biome;
import loom.entity.other.Particle;

import java.awt.*;

/**
 * Used to duck type {@link Biome}, implementing its methods.
 */
public interface BiomeReference {
    String name();

    String inGameName();

    Color color();

    default Particle particle() {
        return null;
    }

    default SoundReference sound() {
        return null;
    }
}

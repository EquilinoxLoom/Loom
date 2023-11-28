package equilinox.vanilla;

import equilinox.ducktype.SoundReference;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Enum representing various vanilla sounds in the Equilinox game.
 */
@SuppressWarnings("unused")
public enum VanillaMusic implements SoundReference {
    MENU_SONG("song"), AMBIENCE, AMBITION, DUSK, GREEN_FIELDS, HOME, HORIZONS, ISLAND_PARADISE, MAIN_THEME_FIXED,
    POLY_DREAM, REMINISCENCE, SUNRISE, TRANQUILITY, WALTZ, WANDER, WONDERFUL_WORLD, ZEN;

    final String file;

    VanillaMusic(String file) {
        this.file = file;
        SoundReference.load(this);
    }

    VanillaMusic() {
        this.file = Arrays
                .stream(name().split("_"))
                .map(word -> word.charAt(0) + word.substring(1))
                .collect(Collectors.joining(" "));
        SoundReference.load(this);
    }

    @Override
    public String id() {
        return file;
    }

    @Override
    public String path() {
        return id() + ".ogg";
    }
}

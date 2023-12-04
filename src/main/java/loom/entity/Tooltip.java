package loom.entity;

import loom.equilinox.ducktype.SoundReference;
import loom.equilinox.vanilla.VanillaSound;

public interface Tooltip {
    String name();

    String description();

    int price();

    default int dpPerMin() {
        return 0;
    }

    int range();

    default SoundReference sound() {
        return VanillaSound.THUD;
    }
}

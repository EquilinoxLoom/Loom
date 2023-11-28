package loom.entity.living;

import equilinox.ducktype.SoundReference;
import equilinox.vanilla.VanillaSound;

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

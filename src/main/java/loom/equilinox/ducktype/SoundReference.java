package loom.equilinox.ducktype;

import audio.Sound;
import resourceManagement.SoundCache;

public interface SoundReference {
    String id();

    default String path() {
        return id() + ".wav";
    }

    default boolean waits() {
        return true;
    }

    static Sound load(SoundReference reference) {
        return SoundCache.CACHE.requestSound(reference.path(), reference.waits(), true);
    }
}

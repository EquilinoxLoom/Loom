package equilinox.ducktype;

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
        if (reference.path().endsWith(".ogg")) {
            return SoundCache.CACHE.requestSound(reference.path(), reference.waits(), true);
        } else if (reference.path().endsWith(".wav")) {
            return SoundCache.CACHE.requestSound(reference.path(), reference.waits());
        }
        String[] split = reference.path().split("\\.");
        if (split.length < 1) throw new RuntimeException("Sound reference " + reference.path() + " has no extension");
        else if (split.length > 2) throw new RuntimeException("Sound reference " + reference.path() + " has an unsupported dot in it's id");
        else throw new RuntimeException("Extension ." + split[1] + " is not supported in sound " + reference.id());
    }
}

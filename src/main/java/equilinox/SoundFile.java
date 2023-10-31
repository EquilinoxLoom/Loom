package equilinox;

import resourceManagement.SoundCache;

public class SoundFile {
    final String name, path;

    SoundFile(String name, String path) {
        this.name = name;
        this.path = path;

        boolean wav = path.endsWith(".wav");
        boolean ogg = path.endsWith(".ogg");

        if (wav || ogg) SoundCache.CACHE.requestSound(path, true, ogg);
    }

    @Override
    public String toString() {
        return name;
    }
}

package equilinox.mixin;

import equilinoxmodkit.loader.ModLoader;
import equilinoxmodkit.mod.EquilinoxMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import utils.MyFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static loom.mod.LoomMod.MOD_POINTER;

@Mixin(value = MyFile.class, remap = false)
public class MixinMyFile {
    @Shadow
    private String path;

    /**
     * @author Sandaliaball
     * @reason Support getting resources from other jar's as streams and support direct String stream
     */
    @Overwrite
    public InputStream getInputStream() {
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            if (path.matches("^.*" + MOD_POINTER + ".+:.*$")) {
                String[] _path = path.replaceAll("^.*" + MOD_POINTER, "").split(":");
                String name = _path[0], file = _path[1];

                EquilinoxMod mod = ModLoader.getLoadedMods().stream()
                        .filter(e -> e.getModInfo().id().equals(name)).toArray(EquilinoxMod[]::new)[0];
                return mod.getClass().getResourceAsStream(file);
            } else if (path.length() > 1000) {
                return new ByteArrayInputStream(path.substring(1).getBytes(StandardCharsets.UTF_8));
            } else return null;
        } else return stream;
    }
}

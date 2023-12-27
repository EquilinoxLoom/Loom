package loom.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import utils.MyFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import loom.LoomMod;

import static loom.LoomMod.*;

@Mixin(value = MyFile.class, remap = false)
public class MixinMyFile {
    @Shadow private String path;

    @Inject(method = "getInputStream", at = @At("RETURN"), cancellable = true)
    public void resourcesProxy(CallbackInfoReturnable<InputStream> cir) {
        if (path.matches("^.*" + MOD_POINTER + ".+:.*$")) {
            String[] _path = path.replaceAll("^.*" + MOD_POINTER, "").split(":");
            String name = _path[0], file = _path[1];

            LoomMod mod = LOOMS.stream().filter(e -> e.getInfo().id().equals(name)).toArray(LoomMod[]::new)[0];
            cir.setReturnValue(mod.getClass().getResourceAsStream(file));
        } else if (path.length() > 1000) {
            cir.setReturnValue(new ByteArrayInputStream(path.substring(1).getBytes(StandardCharsets.UTF_8)));
        }
    }
}

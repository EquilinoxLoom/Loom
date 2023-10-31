package equilinox.mixin;

import equilinoxmodkit.loader.ModLoader;
import languages.GameText;
import loom.mod.LoomMod;
import loom.mod.LoomRegistry;
import main.MainApp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MainApp.class, remap = false, priority = 1001)
public class MixinMainApp {
    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lerrors/ErrorManager;init(Ljava/io/File;Ljava/lang/String;)V", shift = At.Shift.BEFORE))
    private static void preMain(String[] args, CallbackInfo c) {
        GameText.init(0);
        try {
            for (Object loadedMod : ModLoader.getLoadedMods()) {
                LoomMod mod = (LoomMod) Thread.currentThread().getContextClassLoader()
                        .loadClass(loadedMod.getClass().getName()).newInstance();
                mod.registry(new LoomRegistry(mod));
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(method = "main", at = @At(value = "INVOKE", target = "LgameManaging/GameManager;init()V", shift = At.Shift.AFTER))
    private static void debug(String[] args, CallbackInfo c) {
        LoomMod.getLooms().forEach(LoomMod::postRegistry);
    }
}

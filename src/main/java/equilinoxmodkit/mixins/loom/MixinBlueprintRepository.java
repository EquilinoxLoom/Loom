package equilinoxmodkit.mixins.loom;

import blueprints.Blueprint;
import equilinoxmodkit.loader.LaunchHelper;
import loom.LoomMod;
import main.MainApp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import resourceManagement.BlueprintRepository;
import utils.MyFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = BlueprintRepository.class, remap = false)
public abstract class MixinBlueprintRepository {
    @Shadow private static Map<Integer, Blueprint> blueprints = new HashMap<>();

    @Inject(method = "loadAllBlueprints", at = @At(value = "TAIL"))
    private static void loadAllCustom(boolean backgroundLoad, CallbackInfo ci) {
        List<LoomMod> mods;
        try {
            Method method = MainApp.class.getDeclaredMethod("loom$getLooms");
            method.setAccessible(true);
            mods = (List<LoomMod>) method.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        mods.forEach(mod -> mod.getEntities().forEach((id, entity) -> {
            String str = entity.build();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(LaunchHelper.getModsDir(), "para.txt").getPath()))) {
                writer.write(str);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            MyFile blueprintFile = new MyFile(str);
            Blueprint blueprint = Blueprint.load(id, blueprintFile, backgroundLoad);
            blueprints.put(id, blueprint);
        }));
    }
}

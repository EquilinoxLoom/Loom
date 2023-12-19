package equilinoxmodkit.mixins.loom;

import blueprints.Blueprint;
import equilinoxmodkit.loader.LaunchHelper;
import equilinoxmodkit.util.EmkLogger;
import loom.LoomMod;
import loom.entity.Entity;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Mixin(value = BlueprintRepository.class, remap = false)
public abstract class MixinBlueprintRepository {
    @Shadow private static Map<Integer, Blueprint> blueprints = new HashMap<>();

    @Inject(method = "loadAllBlueprints", at = @At(value = "TAIL"))
    private static void loadAllCustom(boolean backgroundLoad, CallbackInfo ci) {
        LoomMod.LOOMS.forEach(mod -> {
            Map<Integer, Entity> entities = mod.getEntities();
            if (entities.isEmpty()) return;
            EmkLogger.log(mod.getModInfo().name() + " - loading entities");
            entities.forEach((id, entity) -> {
                String build = entity.build();
                File log = new File(LaunchHelper.getEquilinoxDir(), "entities");
                if (log.mkdir()) EmkLogger.log("Entities log folder created.");
                File file = new File(log, entity.getClass().getSimpleName().toLowerCase(Locale.ROOT) + ".txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()))) {
                    writer.write(build);
                } catch (IOException e) {
                    EmkLogger.warn("Attempt to create file " + file.getName() + " failed: " + e.getMessage());
                }
                EmkLogger.log(mod.getModInfo().name() + " - loading " + entity.getClass().getSimpleName() + ".class");
                MyFile blueprintFile = new MyFile(build);
                Blueprint blueprint = Blueprint.load(id, blueprintFile, backgroundLoad);
                blueprints.put(id, blueprint);
            });
        });
    }
}

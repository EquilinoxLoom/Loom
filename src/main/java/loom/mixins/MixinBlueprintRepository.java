package loom.mixins;

import blueprints.Blueprint;
import loom.LoomMod;
import loom.entity.Entity;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.fabricmc.loader.impl.util.log.LogLevel;
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
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Mixin(value = BlueprintRepository.class, remap = false)
public abstract class MixinBlueprintRepository {
    @Shadow private static final Map<Integer, Blueprint> blueprints = new HashMap<>();

    @Inject(method = "loadAllBlueprints", at = @At(value = "TAIL"))
    private static void loadAllCustom(boolean backgroundLoad, CallbackInfo ci) {
        LoomMod.LOOMS.forEach(mod -> {
            Map<Integer, Entity> entities = mod.getEntities();
            if (entities.isEmpty()) return;
            Log.log(LogLevel.INFO, LogCategory.LOG, mod.getInfo().name() + " - loading entities");
            entities.forEach((id, entity) -> {
                String build = entity.build();
                File log = Paths.get("/entities").toFile();
                if (log.mkdir()) Log.log(LogLevel.INFO, LogCategory.LOG, "Entities log folder created.");
                File file = new File(log, entity.getClass().getSimpleName().toLowerCase(Locale.ROOT) + ".txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()))) {
                    writer.write(build);
                } catch (IOException e) {
                    Log.warn(LogCategory.LOG, "Attempt to create file " + file.getName() + " failed: " + e.getMessage());
                }
                Log.log(LogLevel.INFO, LogCategory.LOG, mod.getInfo().name() + " - loading " + entity.getClass().getSimpleName() + ".class");
                blueprints.put(id, Blueprint.load(id, new MyFile(build), backgroundLoad));
            });
        });
    }
}

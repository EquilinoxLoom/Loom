package equilinox.mixin;

import blueprints.Blueprint;
import loom.entity.weaver.EntityProcessor;
import loom.mod.LoomMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import resourceManagement.BlueprintRepository;
import utils.MyFile;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = BlueprintRepository.class, remap = false)
public abstract class MixinBlueprintRepository {
    @Shadow private static Map<Integer, Blueprint> blueprints = new HashMap<>();

    @Inject(method = "loadAllBlueprints", at = @At(value = "TAIL"))
    private static void loadAllCustom(boolean backgroundLoad, CallbackInfo ci) {
        LoomMod.getLooms().forEach(mod -> mod.getEntities().forEach((id, entity) -> {
            MyFile blueprintFile = new MyFile(new EntityProcessor(entity).build());
            Blueprint blueprint = Blueprint.load(id, blueprintFile, backgroundLoad);
            blueprints.put(id, blueprint);
        }));
    }
}

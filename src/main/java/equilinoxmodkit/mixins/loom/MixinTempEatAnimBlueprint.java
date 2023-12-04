package equilinoxmodkit.mixins.loom;

import baseMovement.MovementComp;
import eating.EatingAnimation;
import eating.StandardEatingAi;
import eating.TempEatAnimBlueprint;
import loom.LoomMod;
import main.MainApp;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toolbox.Transformation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Mixin(value = TempEatAnimBlueprint.class, remap = false)
public class MixinTempEatAnimBlueprint {
    @Final @Shadow private int id;

    @Inject(method = "createInstance", at = @At(value = "RETURN", ordinal = 4), cancellable = true)
    public void createInstance(Transformation transform, MovementComp mover, StandardEatingAi eater,
                               CallbackInfoReturnable<EatingAnimation> cir) {
        List<LoomMod> mods;
        try {
            Method method = MainApp.class.getDeclaredMethod("loom$getLooms");
            method.setAccessible(true);
            mods = (List<LoomMod>) method.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        mods.forEach(mod -> mod.getEatingAnimations().forEach(anim -> {
            if (this.id == anim.hashCode()) {
                anim.create(transform, mover, eater);
                cir.setReturnValue(anim);
            }
        }));
    }
}

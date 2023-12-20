package loom.mixins;

import baseMovement.MovementComp;
import eating.EatingAnimation;
import eating.StandardEatingAi;
import eating.TempEatAnimBlueprint;
import loom.LoomMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toolbox.Transformation;

@Mixin(value = TempEatAnimBlueprint.class, remap = false)
public class MixinTempEatAnimBlueprint {
    @Final @Shadow private int id;

    @Inject(method = "createInstance", at = @At(value = "RETURN", ordinal = 4), cancellable = true)
    public void createInstance(Transformation transform, MovementComp mover, StandardEatingAi eater,
                               CallbackInfoReturnable<EatingAnimation> cir) {
        LoomMod.LOOMS.forEach(mod -> mod.getEatingAnimations().forEach(anim -> {
            if (this.id == anim.hashCode()) {
                anim.create(transform, mover, eater);
                cir.setReturnValue(anim);
            }
        }));
    }
}

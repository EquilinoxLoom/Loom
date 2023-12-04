package equilinoxmodkit.mixins.loom;

import basics.Loader;
import blueprints.Blueprint;
import blueprints.SubBlueprint;
import equilinoxmodkit.util.EmkLogger;
import loom.LoomMod;
import org.lwjgl.BufferUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import profile3d.ProfileImageRenderer;

import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;

@Mixin(value = ProfileImageRenderer.class, remap = false)
public class MixinProfileImageRenderer {
    @Shadow private FloatBuffer floatBuffer;

    @Shadow private Blueprint blueprint;

    @Shadow private SubBlueprint model;

    @Shadow private int vbo, startingVertex;

    @Shadow private float scale;

    @Shadow private void setMaterial() {}

    @Inject(method = "<init>", at = @At("TAIL"))
    public void increaseBuffer(CallbackInfo ci) {
        floatBuffer = BufferUtils.createFloatBuffer(LoomMod.BUFFER_SIZE);
    }

    /**
     * @author Sandáliaball
     * @reason print mesh name on overflow
     */
    @Overwrite
    public void changeModel(Blueprint blueprint) {
        this.blueprint = blueprint;
        this.model = blueprint.getMainSubBlueprint();
        try {
            Loader.refillVboWithData(this.vbo, this.floatBuffer, model.getFullModelData());
        } catch (BufferOverflowException e) {
            EmkLogger.warn("Model of entity " + blueprint.getName() + " has too many vertices.");
        }
        setMaterial();
        this.scale = 10.0F / blueprint.getMaxSize();
        this.startingVertex = 0;
    }
}

package loom.mixins;

import basics.Loader;
import blueprints.Blueprint;
import blueprints.SubBlueprint;
import iconGenerator.IconRenderer;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;

@Mixin(value = IconRenderer.class, remap = false)
public class MixinIconRenderer {
    @Shadow private FloatBuffer floatBuffer;

    @Shadow private int vbo;

    /**
     * @author Sandáliaball
     * @reason print mesh name on overflow
     */
    @Overwrite
    private int storeMeshData(Blueprint blueprint) {
        SubBlueprint model = blueprint.getMainSubBlueprint();
        try {
            Loader.refillVboWithData(this.vbo, this.floatBuffer, model.getFullModelData());
        } catch (BufferOverflowException e) {
            Log.warn(LogCategory.LOG, "Model of entity " + blueprint.getName() + " has too many vertices");
        }
        return model.getVertexCount();
    }
}

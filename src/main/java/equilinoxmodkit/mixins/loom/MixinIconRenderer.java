package equilinoxmodkit.mixins.loom;

import basics.Loader;
import blueprints.Blueprint;
import blueprints.SubBlueprint;
import equilinoxmodkit.util.EmkLogger;
import iconGenerator.IconRenderer;
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
            EmkLogger.warn("Model of entity " + blueprint.getName() + " has too many vertices");
        }
        return model.getVertexCount();
    }
}

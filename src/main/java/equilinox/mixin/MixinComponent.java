package equilinox.mixin;

import componentArchitecture.ComponentBlueprint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = MixinComponent.class, remap = false)
public class MixinComponent {
    @Shadow private ComponentBlueprint blueprint;

    @Unique protected void loom$setBlueprint(ComponentBlueprint blueprint) {
        this.blueprint = blueprint;
    }
}

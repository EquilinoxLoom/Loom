package equilinoxmodkit.mixins.loom;

import componentArchitecture.ComponentLoader;
import componentArchitecture.ComponentType;
import loom.LoomMod;
import main.MainApp;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Mixin(value = ComponentType.class, remap = false)
public class MixinComponentType {

    @SuppressWarnings("InvokerTarget")
    @Invoker("<init>")
    private static ComponentType newComponentType(String internalName, int internalId, ComponentLoader loader, boolean active, boolean dynamic) {
        throw new AssertionError();
    }

    @SuppressWarnings("ShadowTarget")
    @Shadow private static @Final @Mutable ComponentType[] ENUM$VALUES;

    @Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "LcomponentArchitecture/ComponentType;ENUM$VALUES:[LcomponentArchitecture/ComponentType;", shift = At.Shift.AFTER))
    private static void addCustomComponentType(CallbackInfo ci) {
        List<LoomMod> mods;
        try {
            Method method = MainApp.class.getDeclaredMethod("loom$getLooms");
            method.setAccessible(true);
            mods = (List<LoomMod>) method.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        List<ComponentType> types = new ArrayList<>(Arrays.asList(ENUM$VALUES));
        mods.stream().map(LoomMod::getComponents).flatMap(Collection::stream)
                .forEach(component -> {
                    ComponentType type = newComponentType(component.name(), types.size(), component.getLoader(),
                            component.isActive(), component.isDynamic());
                    types.add(type);
                    component.setBlueprintType(type);
                });
        ENUM$VALUES = types.toArray(new ComponentType[0]);
    }
}

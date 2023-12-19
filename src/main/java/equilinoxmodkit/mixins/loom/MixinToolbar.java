package equilinoxmodkit.mixins.loom;

import main.MainApp;
import mainGuis.GuiRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import textures.Texture;
import toolTips.ToolTipInfo;
import toolbar.Toolbar;
import userInterfaces.ClickListener;
import userInterfaces.GuiClickEvent;
import userInterfaces.TabButtonUi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(value = Toolbar.class, remap = false)
public class MixinToolbar {
    @Shadow
    private TabButtonUi addButton(Texture icon, float xPos, ClickListener listener) {
        return null;
    }

    @Inject(method = "addButtons()V", at = @At("TAIL"))
    private void addButtons(CallbackInfo c) {
        loom$addCheatButton();
    }

    @Unique private void loom$addCheatButton() {
        new ClickListener() {
            public final TabButtonUi button = addButton(GuiRepository.TASK_ICON, 1.5F, this);

            {
                button.setToolTip(ToolTipInfo.newInfo("CHEAT", "CHEAT"));
            }

            @Override
            public void eventOccurred(GuiClickEvent guiClickEvent) {
                if (guiClickEvent.isToggleOn()) {
                    try {
                        Method cheat = MainApp.class.getDeclaredMethod("loom$cheat");
                        cheat.setAccessible(true);
                        cheat.invoke(null);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
    }
}

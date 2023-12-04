package equilinoxmodkit.mixins;

import equilinoxmodkit.uis.ModListUi;
import gameMenu.DnaButtonGui;
import gameMenu.GameMenuBackground;
import gameMenu.GameMenuGui;
import gameMenu.MenuPanelGui;
import guis.GuiTexture;
import mainGuis.GuiRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import userInterfaces.ClickListener;
import userInterfaces.GuiImage;

import java.util.List;

@Mixin(value = {MenuPanelGui.class}, remap = false)
public abstract class MixinMenuPanelGui {
	@Shadow private GameMenuGui gameMenu;

	@Shadow private GameMenuBackground superMenu;

	@Shadow private List<DnaButtonGui> buttons;

	@Unique private DnaButtonGui load$modsButton;

	@Shadow
	protected abstract DnaButtonGui addLine(int paramInt1, int paramInt2, int paramInt3, String paramString, ClickListener paramClickListener);

	@Shadow
	protected abstract DnaButtonGui addButton(int paramInt, GuiTexture paramGuiTexture, String paramString, ClickListener paramClickListener);

	@Inject(method = {"init"}, at = {@At(value = "INVOKE", target = "LgameMenu/MenuPanelGui;updateNewWorldButton()V", shift = At.Shift.BEFORE)})
	private void init(CallbackInfo ci) {
		this.load$modsButton = addButton(8, new GuiImage(GuiRepository.LINES[3]).getTexture(), "Mods", event -> {
			if (event.isLeftClick()) this.gameMenu.setNewSecondaryScreen(new ModListUi(this.gameMenu));
		});
		this.buttons.add(this.load$modsButton);
	}
}

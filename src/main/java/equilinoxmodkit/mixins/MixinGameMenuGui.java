package equilinoxmodkit.mixins;

import fontRendering.Text;
import gameMenu.GameMenuGui;
import guis.GuiComponent;
import mainGuis.ColourPalette;
import mainGuis.UiSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = {GameMenuGui.class}, remap = false)
public abstract class MixinGameMenuGui extends GuiComponent {
	@Overwrite
	private void addVersion() {
		Text text = Text.newText("Version 1.3.0 (modded)").setFontSize(UiSettings.LARGE_FONT).create();
		text.setColour(ColourPalette.WHITE);
		addText(text, 0.64F, 0.3F, 1.0F);
	}
}

package equilinoxmodkit.uis;

import equilinoxmodkit.loader.ModLoader;
import equilinoxmodkit.mod.EquilinoxMod;
import equilinoxmodkit.mod.ModInfo;
import fontRendering.Text;
import gameMenu.GameMenuBackground;
import gameMenu.GameMenuGui;
import guiRendering.GuiRenderData;
import mainGuis.ColourPalette;
import org.lwjgl.util.vector.Vector2f;
import textures.Texture;
import userInterfaces.ClickListener;
import userInterfaces.GuiImage;
import userInterfaces.GuiPanel;
import userInterfaces.IconButtonUi;
import utils.MyFile;

public class ModListUi extends GuiPanel {
	private final GameMenuGui gameMenu;

	public ModListUi(GameMenuGui gameMenu) {
		super(GameMenuBackground.getStandardColour(), 0.65F);
        this.gameMenu = gameMenu;
	}


	protected void init() {
		super.init();
		createBackOption();
		float yPos = 0.1F;
		for (EquilinoxMod mod : ModLoader.getLoadedMods()) {
			float oldY = yPos;
			ModInfo info = mod.getModInfo();
			float nameWidth = addModName(yPos, info.name());
			addModVersion(yPos, info.version(), nameWidth);
			yPos += 0.05F;
			yPos += addModDescription(yPos, info.description());
			yPos += 0.065F;
			float newY = yPos;
			if (!info.thumbnail().isEmpty()) {
				GuiImage thumbnail = new GuiImage(Texture.newTexture(new MyFile(info.thumbnail())).create());
				addComponent(thumbnail, 0.25F - pixelsToRelativeX(72.0F), yPos - (newY - oldY) / 2.0F - pixelsToRelativeY(32.0F), pixelsToRelativeX(64.0F), pixelsToRelativeY(64.0F));
			}
		}
	}

	protected void updateGuiTexturePositions(Vector2f position, Vector2f scale) {}

	protected void updateSelf() {}

	protected void getGuiTextures(GuiRenderData data) {}

	private float addModName(float yPos, String name) {
		Text text = Text.newText(name).setFontSize(0.9F).create();
		text.setColour(ColourPalette.MIDDLE_GREY);
		addText(text, 0.25F, yPos, 1.0F);
		return text.getActualWidth();
	}

	private void addModVersion(float yPos, String version, float nameWidth) {
		Text text = Text.newText("v" + version).setFontSize(0.8F).create();
		text.setColour(ColourPalette.LIGHT_GREY);
		addText(text, 0.25F + nameWidth + 0.025F, yPos + 0.01F, 0.8F);
	}

	private float addModDescription(float yPos, String description) {
		if (description.length() < 100) {
			Text text1 = Text.newText(description).setFontSize(0.8F).create();
			text1.setColour(ColourPalette.LIGHT_GREY);
			addText(text1, 0.25F, yPos, 0.8F);
			return 0.0F;
		}
		String[] tokens = description.split(" ");
		StringBuilder s1 = new StringBuilder();
		StringBuilder s2 = new StringBuilder();
		for (String token : tokens) {
			if (s1.length() + token.length() + 1 <= 100) {
				s1.append(token).append(" ");
			} else {
				s2.append(token).append(" ");
			}
		}
		Text text = Text.newText(s1.toString()).setFontSize(0.8F).create();
		text.setColour(ColourPalette.LIGHT_GREY);
		addText(text, 0.25F, yPos, 0.8F);
		Text text2 = Text.newText(s2.toString()).setFontSize(0.8F).create();
		text2.setColour(ColourPalette.LIGHT_GREY);
		addText(text2, 0.25F, yPos + 0.04F, 0.8F);
		return 0.04F;
	}

	private void createBackOption() {
		IconButtonUi backButton = new IconButtonUi(GameMenuGui.BACK_ICON, ColourPalette.DARK_GREY, ColourPalette.MIDDLE_GREY);
		addComponentY(backButton, GameMenuGui.BACK_BUTTON_POS.x, GameMenuGui.BACK_BUTTON_POS.y, 0.12F);
		ClickListener listener = event -> {
			if (event.isLeftClick())
				this.gameMenu.closeSecondaryScreen();
		};
		backButton.addListener(listener);
	}
}

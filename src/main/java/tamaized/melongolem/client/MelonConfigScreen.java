package tamaized.melongolem.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import tamaized.melongolem.MelonConfig;
import tamaized.melongolem.MelonMod;

public class MelonConfigScreen extends Screen {

	private final Screen parent;
	private TextFieldWidget input;
	private CheckboxButton enabled;
	private static final String TEXT_DONATOR_COLOR = "Donator Color:";

	public MelonConfigScreen(Minecraft mc, Screen parent) {
		super(new TranslationTextComponent(MelonMod.MODID + "_config"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		float sw = Minecraft.getInstance().getWindow().getScaledWidth();
		int w = 396;
		float x = (sw - w);
		x -= x / 2F;
		addButton(new ExtendedButton((int) x, Minecraft.getInstance().getWindow().getScaledHeight() - 25, w, 20, "Close", button -> Minecraft.getInstance().displayGuiScreen(parent)));
		int ix = font.getStringWidth(TEXT_DONATOR_COLOR) + 10;
		addButton(input = new TextFieldWidget(font, ix, 25, Minecraft.getInstance().getWindow().getScaledWidth() - ix - 15, 20, ""));
		input.setText(MelonMod.config.DONATOR_SETTINGS.color.get());
		addButton(enabled = new CheckboxButton(ix, 50, 20, 20, "Enabled", MelonMod.config.DONATOR_SETTINGS.enable.get()));

	}

	@Override
	public void removed() {
		MelonMod.config.DONATOR_SETTINGS.enable.set(enabled.isChecked());
		MelonMod.config.DONATOR_SETTINGS.enable.save();
		MelonMod.config.DONATOR_SETTINGS.color.set(input.getText());
		MelonMod.config.DONATOR_SETTINGS.color.save();
		MelonConfig.setupColor();
		super.removed();
	}

	@Override
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		renderBackground();
		drawCenteredString(font, "Melon Golem Config", (int) (Minecraft.getInstance().getWindow().getScaledWidth() / 2F), 5, 0xFFFFFFFF);
		int color = 0xFFFFFFFF;
		try {
			color = Integer.decode(input.getText());
		} catch (NumberFormatException e) {
			// NO-OP
		}
		GlStateManager.enableBlend();
		drawString(font, TEXT_DONATOR_COLOR, 5, 25 + font.FONT_HEIGHT / 2 + 1, color);
		GlStateManager.disableBlend();
		super.render(p_render_1_, p_render_2_, p_render_3_);
	}
}

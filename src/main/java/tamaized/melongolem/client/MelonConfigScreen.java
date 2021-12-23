package tamaized.melongolem.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import tamaized.melongolem.MelonConfig;
import tamaized.melongolem.MelonMod;

public class MelonConfigScreen extends Screen {

	private final Screen parent;
	private EditBox input;
	private Checkbox enabled;
	private static final String TEXT_DONATOR_COLOR = "Donator Color:";

	public MelonConfigScreen(Minecraft mc, Screen parent) {
		super(new TranslatableComponent(MelonMod.MODID + "_config"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		float sw = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int w = 396;
		float x = (sw - w);
		x -= x / 2F;
		addRenderableWidget(new ExtendedButton((int) x, Minecraft.getInstance().getWindow().getGuiScaledHeight() - 25, w, 20, new TextComponent("Close"), button -> Minecraft.getInstance().setScreen(parent)));
		int ix = font.width(TEXT_DONATOR_COLOR) + 10;
		addRenderableWidget(input = new EditBox(font, ix, 25, Minecraft.getInstance().getWindow().getGuiScaledWidth() - ix - 15, 20, new TextComponent("")));
		input.setValue(MelonMod.config.DONATOR_SETTINGS.color.get());
		addRenderableWidget(enabled = new Checkbox(ix, 50, 20, 20, new TextComponent("Enabled"), MelonMod.config.DONATOR_SETTINGS.enable.get()));

	}

	@Override
	public void onClose() {
		MelonMod.config.DONATOR_SETTINGS.enable.set(enabled.selected());
		MelonMod.config.DONATOR_SETTINGS.enable.save();
		MelonMod.config.DONATOR_SETTINGS.color.set(input.getValue());
		MelonMod.config.DONATOR_SETTINGS.color.save();
		MelonConfig.setupColor();
		super.onClose();
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		drawCenteredString(matrixStack, font, new TextComponent("Melon Golem Config"), (int) (Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2F), 5, 0xFFFFFFFF);
		int color = 0xFFFFFFFF;
		try {
			color = Integer.decode(input.getValue());
		} catch (NumberFormatException e) {
			// NO-OP
		}
		GlStateManager._enableBlend();
		drawString(matrixStack, font, TEXT_DONATOR_COLOR, 5, 25 + font.lineHeight / 2 + 1, color);
		GlStateManager._disableBlend();
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}

package tamaized.melongolem.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import tamaized.melongolem.MelonConfig;
import tamaized.melongolem.MelonMod;

public class MelonConfigScreen extends Screen {

	private final Screen parent;
	private EditBox input;
	private Checkbox enabled;
	private static final String TEXT_DONATOR_COLOR = "Donator Color:";

	public MelonConfigScreen(Minecraft mc, Screen parent) {
		super(Component.translatable(MelonMod.MODID + "_config"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		float sw = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int w = 396;
		float x = (sw - w);
		x -= x / 2F;
		addRenderableWidget(new ExtendedButton((int) x, Minecraft.getInstance().getWindow().getGuiScaledHeight() - 25, w, 20, Component.literal("Close"), button -> Minecraft.getInstance().setScreen(parent)));
		int ix = font.width(TEXT_DONATOR_COLOR) + 10;
		addRenderableWidget(input = new EditBox(font, ix, 25, Minecraft.getInstance().getWindow().getGuiScaledWidth() - ix - 15, 20, Component.literal("")));
		input.setValue(MelonMod.config.DONATOR_SETTINGS.color.get());
		addRenderableWidget(enabled = new Checkbox(ix, 50, 20, 20, Component.literal("Enabled"), MelonMod.config.DONATOR_SETTINGS.enable.get()));

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
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		renderBackground(graphics);
		graphics.drawCenteredString(font, Component.literal("Melon Golem Config"), (int) (Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2F), 5, 0xFFFFFFFF);
		int color = 0xFFFFFFFF;
		try {
			color = Integer.decode(input.getValue());
		} catch (NumberFormatException e) {
			// NO-OP
		}
		RenderSystem.enableBlend();
		graphics.drawString(font, TEXT_DONATOR_COLOR, 5, 25 + font.lineHeight / 2 + 1, color, false);
		RenderSystem.disableBlend();
		super.render(graphics, mouseX, mouseY, partialTicks);
	}
}

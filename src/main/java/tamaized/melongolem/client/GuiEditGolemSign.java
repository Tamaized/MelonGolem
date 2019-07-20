package tamaized.melongolem.client;


import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.network.server.ServerPacketHandlerMelonSign;

import java.util.Objects;

public class GuiEditGolemSign extends Screen {

	private final IModProxy.ISignHolder golem;
	private int updateCounter;
	private int editLine;
	private boolean canSend = true;
	private TextInputUtil field_214267_d;

	public GuiEditGolemSign(IModProxy.ISignHolder golem) {
		super(new TranslationTextComponent("melongolemsignholder"));
		this.golem = golem;
	}

	@Override
	protected void init() {
		super.init();
		Objects.requireNonNull(minecraft).keyboardListener.enableRepeatEvents(true);
		this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, I18n.format("gui.done"), (p_214266_1_) -> {
			this.close();
		}));
		this.field_214267_d = new TextInputUtil(this.minecraft, () -> golem.getSignText(this.editLine).getString(), (p_214265_1_) -> golem.setSignText(this.editLine, new StringTextComponent(p_214265_1_)), 90);
	}

	@Override
	public void removed() {
		Objects.requireNonNull(minecraft).keyboardListener.enableRepeatEvents(false);
		if (canSend)
			MelonMod.network.sendToServer(new ServerPacketHandlerMelonSign(golem));
	}

	private void close() {
		Objects.requireNonNull(minecraft).displayGuiScreen(null);
	}

	@Override
	public void tick() {
		++this.updateCounter;
		if (golem.getDistance(Minecraft.getInstance().player) > 6) {
			canSend = false;
			close();
		}
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		this.field_214267_d.func_216894_a(typedChar);
		return true;
	}

	@Override
	public void onClose() {
		close();
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (p_keyPressed_1_ == 265) {
			this.editLine = this.editLine - 1 & 3;
			this.field_214267_d.func_216899_b();
			return true;
		} else if (p_keyPressed_1_ != 264 && p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
			return this.field_214267_d.func_216897_a(p_keyPressed_1_) || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
		} else {
			this.editLine = this.editLine + 1 & 3;
			this.field_214267_d.func_216899_b();
			return true;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		this.drawCenteredString(this.font, I18n.format("sign.edit"), this.width / 2, 40, 0xFFFFFF);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float) (this.width / 2), 0.0F, 50.0F);
		//		float f = 93.75F;
		GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
		/*Block block = golem.getBlockType();

		if (block == Blocks.STANDING_SIGN) {
			float f1 = (float) (this.tileSign.getBlockMetadata() * 360) / 16.0F;
			GlStateManager.rotatef(f1, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(0.0F, -1.0625F, 0.0F);
		} else {
			int i = this.tileSign.getBlockMetadata();
			float f2 = 0.0F;

			if (i == 2) {
				f2 = 180.0F;
			}

			if (i == 4) {
				f2 = 90.0F;
			}

			if (i == 5) {
				f2 = -90.0F;
			}

			GlStateManager.rotatef(f2, 0.0F, 1.0F, 0.0F);
		}*/
		GlStateManager.translatef(0.0F, -1.0625F, 0.0F);

		EntityMelonGolem.te.func_214062_a(this.editLine, this.field_214267_d.func_216896_c(), this.field_214267_d.func_216898_d(), this.updateCounter / 6 % 2 == 0);
		TileEntityRendererDispatcher.instance.getRenderer(SignTileEntity.class).render(EntityMelonGolem.te, -0.5D, -0.75D, -0.5D, 0.0F, -1);
		EntityMelonGolem.te.func_214063_g();
		GlStateManager.popMatrix();
		super.render(mouseX, mouseY, partialTicks);
	}


}

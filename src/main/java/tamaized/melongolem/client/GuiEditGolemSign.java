package tamaized.melongolem.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.TextComponentString;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.network.server.ServerPacketHandlerMelonSign;

public class GuiEditGolemSign extends GuiScreen {

	private final IModProxy.ISignHolder golem;
	private int updateCounter;
	private int editLine;
	private GuiButton doneBtn;
	private boolean canSend = true;

	public GuiEditGolemSign(IModProxy.ISignHolder golem) {
		this.golem = golem;
	}

	@Override
	public void initGui() {
		this.mc.keyboardListener.enableRepeatEvents(true);
		this.doneBtn = this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done", new Object[0])) {
			@Override
			public void onClick(double p_194829_1_, double p_194829_3_) {
				mc.displayGuiScreen(null);
			}
		});
	}

	@Override
	public void onGuiClosed() {
		this.mc.keyboardListener.enableRepeatEvents(false);
		if (canSend)
			MelonMod.network.sendToServer(new ServerPacketHandlerMelonSign(golem));
	}

	@Override
	public void close() {
		onGuiClosed();
	}

	@Override
	public void tick() {
		++this.updateCounter;
		if (golem.getDistance(Minecraft.getInstance().player) > 6) {
			canSend = false;
			Minecraft.getInstance().player.closeScreen();
		}
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		String lvt_3_1_ = golem.getSignText(editLine).getString();
		if (SharedConstants.isAllowedCharacter(typedChar) && this.fontRenderer.getStringWidth(lvt_3_1_ + typedChar) <= 90) {
			lvt_3_1_ = lvt_3_1_ + typedChar;
		}

		golem.setSignText(editLine, new TextComponentString(lvt_3_1_));
		return true;
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (p_keyPressed_1_ == 265) {
			this.editLine = this.editLine - 1 & 3;
			return true;
		} else if (p_keyPressed_1_ != 264 && p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
			if (p_keyPressed_1_ == 259) {
				String lvt_4_1_ = golem.getSignText(editLine).getString();
				if (!lvt_4_1_.isEmpty()) {
					lvt_4_1_ = lvt_4_1_.substring(0, lvt_4_1_.length() - 1);
					golem.setSignText(this.editLine, new TextComponentString(lvt_4_1_));
				}

				return true;
			} else {
				return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
			}
		} else {
			this.editLine = this.editLine + 1 & 3;
			return true;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format("sign.edit"), this.width / 2, 40, 16777215);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float) (this.width / 2), 0.0F, 50.0F);
		//		float f = 93.75F;
		GlStateManager.scalef(-93.75F, -93.75F, -93.75F);
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
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

		if (this.updateCounter / 6 % 2 == 0) {
			EntityMelonGolem.te.lineBeingEdited = this.editLine;
		}

		for (int index = 0; index < 4; index++)
			EntityMelonGolem.te.signText[index] = golem.getSignText(index);
		TileEntityRendererDispatcher.instance.render(EntityMelonGolem.te, -0.5D, -0.75D, -0.5D, 0.0F);
		EntityMelonGolem.te.lineBeingEdited = -1;
		GlStateManager.popMatrix();
		super.render(mouseX, mouseY, partialTicks);
	}


}

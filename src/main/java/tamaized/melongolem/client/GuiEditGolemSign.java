package tamaized.melongolem.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.network.server.ServerPacketHandlerMelonSign;

import javax.annotation.Nonnull;

public class GuiEditGolemSign extends Screen {

	private SignRenderer.SignModel signModel;
	private final ISignHolder golem;
	private int updateCounter;
	private int editLine;
	private boolean canSend = true;
	private TextFieldHelper textInputUtil;

	public GuiEditGolemSign(ISignHolder golem) {
		super(Component.translatable("sign.edit"));
		this.golem = golem;
	}

	@Override
	protected void init() {
		this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), (p_214266_1_) -> this.onClose()).bounds(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build());
		this.textInputUtil = new TextFieldHelper(

				() -> golem.getSignText(this.editLine).getString(),

				text -> golem.setSignText(editLine, Component.literal(text)),

				TextFieldHelper.createClipboardGetter(this.minecraft),

				TextFieldHelper.createClipboardSetter(this.minecraft),

				(string) -> this.minecraft.font.width(string) <= 90

		);
		signModel = SignRenderer.createSignModel(minecraft.getEntityModels(), ((SignBlock)EntityMelonGolem.SIGN_TILE_BLOCKSTATE.getBlock()).type());
	}

	@Override
	public void onClose() {
		if (minecraft == null)
			return;
		if (canSend)
			MelonMod.network.sendToServer(new ServerPacketHandlerMelonSign(golem));
		this.minecraft.setScreen(null);
	}

	@Override
	public void tick() {
		++this.updateCounter;
		if (golem.distanceTo(Minecraft.getInstance().player) > 6) {
			canSend = false;
			onClose();
		}
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		this.textInputUtil.charTyped(typedChar);
		return true;
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (p_keyPressed_1_ == 265) {
			this.editLine = this.editLine - 1 & 3;
			this.textInputUtil.setCursorToEnd();
			return true;
		} else if (p_keyPressed_1_ != 264 && p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
			return this.textInputUtil.keyPressed(p_keyPressed_1_) || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
		} else {
			this.editLine = this.editLine + 1 & 3;
			this.textInputUtil.setCursorToEnd();
			return true;
		}
	}

	@Override
	public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		Lighting.setupForFlatItems();
		this.renderBackground(graphics);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 16777215);
		PoseStack stack = graphics.pose();
		stack.pushPose();
		stack.translate(this.width / 2, 0.0D, 50.0D);
		stack.scale(93.75F, -93.75F, 93.75F);
		stack.translate(0.0D, -1.3125D, 0.0D);
		BlockState blockstate = EntityMelonGolem.SIGN_TILE_BLOCKSTATE = ((StandingAndWallBlockItem) golem.getHead().getItem()).wallBlock.defaultBlockState();
		stack.translate(0.0D, -0.3125D, 0.0D);

		boolean flag1 = this.updateCounter / 6 % 2 == 0;
		stack.pushPose();
		stack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		MultiBufferSource.BufferSource irendertypebuffer$impl = this.minecraft.renderBuffers().bufferSource();
		Material rendermaterial = Sheets.getSignMaterial(((SignBlock)blockstate.getBlock()).type());
		VertexConsumer ivertexbuilder = rendermaterial.buffer(irendertypebuffer$impl, this.signModel::renderType);
		signModel.stick.visible = false;
		this.signModel.root.render(stack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY);

		stack.popPose();
		stack.translate(0.0D, 0.33333334F, 0.046666667F);
		stack.scale(0.010416667F, -0.010416667F, 0.010416667F);
		int i = DyeColor.BLACK.getTextColor();
		int j = this.textInputUtil.getCursorPos();
		int k = this.textInputUtil.getSelectionPos();
		int l = this.editLine * 10 - 4 * 5;
		Matrix4f matrix4f = stack.last().pose();

		for (int i1 = 0; i1 < 4; ++i1) {
			String s = this.golem.getSignText(i1).getString();
			if (s != null) {
				if (this.font.isBidirectional()) {
					s = this.font.bidirectionalShaping(s);
				}

				float f3 = (float) (-this.minecraft.font.width(s) / 2);
				this.minecraft.font.drawInBatch(s, f3, (float) (i1 * 10 - 4 * 5), i, false, matrix4f, irendertypebuffer$impl, Font.DisplayMode.NORMAL, 0, 15728880, false);
				if (i1 == this.editLine && j >= 0 && flag1) {
					int j1 = this.minecraft.font.width(s.substring(0, Math.min(j, s.length())));
					int k1 = j1 - this.minecraft.font.width(s) / 2;
					if (j >= s.length()) {
						this.minecraft.font.drawInBatch("_", (float) k1, (float) l, i, false, matrix4f, irendertypebuffer$impl, Font.DisplayMode.NORMAL, 0, 15728880, false);
					}
				}
			}
		}

		irendertypebuffer$impl.endBatch();

		for (int i3 = 0; i3 < 4; ++i3) {
			String s1 = this.golem.getSignText(i3).getString();
			if (s1 != null && i3 == this.editLine && j >= 0) {
				int j3 = this.minecraft.font.width(s1.substring(0, Math.max(Math.min(j, s1.length()), 0)));
				int k3 = j3 - this.minecraft.font.width(s1) / 2;
				if (flag1 && j < s1.length()) {
					graphics.fill(k3, l - 1, k3 + 1, l + 9, -16777216 | i);
				}

				if (k != j) {
					int l3 = Math.min(j, k);
					int l1 = Math.max(j, k);
					int i2 = this.minecraft.font.width(s1.substring(0, l3)) - this.minecraft.font.width(s1) / 2;
					int j2 = this.minecraft.font.width(s1.substring(0, l1)) - this.minecraft.font.width(s1) / 2;
					int k2 = Math.min(i2, j2);
					int l2 = Math.max(i2, j2);
					RenderSystem.enableColorLogicOp();
					RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
					graphics.fill(k2, l, l2, l + 9, -16776961);
					RenderSystem.disableColorLogicOp();
				}
			}
		}

		stack.popPose();
		Lighting.setupFor3DItems();
		super.render(graphics, mouseX, mouseY, partialTicks);
	}
}

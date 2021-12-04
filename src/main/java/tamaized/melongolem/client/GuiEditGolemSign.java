package tamaized.melongolem.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.network.server.ServerPacketHandlerMelonSign;

import javax.annotation.Nonnull;
import java.util.Objects;

public class GuiEditGolemSign extends Screen {

	private final SignRenderer.SignModel signModel = new SignRenderer.SignModel();
	private final IModProxy.ISignHolder golem;
	private int updateCounter;
	private int editLine;
	private boolean canSend = true;
	private TextFieldHelper textInputUtil;

	public GuiEditGolemSign(IModProxy.ISignHolder golem) {
		super(new TranslatableComponent("melongolemsignholder"));
		this.golem = golem;
	}

	@Override
	protected void init() {
		Objects.requireNonNull(minecraft).keyboardHandler.setSendRepeatsToGui(true);
		this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, new TranslatableComponent("gui.done"), (p_214266_1_) -> this.onClose()));
		this.textInputUtil = new TextFieldHelper(

				() -> golem.getSignText(this.editLine).getString(),

				text -> golem.setSignText(editLine, new TextComponent(text)),

				TextFieldHelper.createClipboardGetter(this.minecraft),

				TextFieldHelper.createClipboardSetter(this.minecraft),

				(string) -> this.minecraft.font.width(string) <= 90

		);
	}

	@Override
	public void onClose() {
		if (minecraft == null)
			return;
		Objects.requireNonNull(minecraft).keyboardHandler.setSendRepeatsToGui(false);
		if (canSend)
			MelonMod.network.sendToServer(new ServerPacketHandlerMelonSign(golem));
	}

	@Override
	public void tick() {
		++this.updateCounter;
		if (golem.getDistance(Minecraft.getInstance().player) > 6) {
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
	public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		Lighting.setupForFlatItems();
		this.renderBackground(matrixStack);
		drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 40, 16777215);
		matrixStack.pushPose();
		matrixStack.translate(this.width / 2, 0.0D, 50.0D);
		float f = 93.75F;
		matrixStack.scale(93.75F, -93.75F, 93.75F);
		matrixStack.translate(0.0D, -1.3125D, 0.0D);
		BlockState blockstate = EntityMelonGolem.SIGN_TILE_BLOCKSTATE = ((StandingAndWallBlockItem) golem.getHead().getItem()).wallBlock.defaultBlockState();
		boolean flag = blockstate.getBlock() instanceof StandingSignBlock;
		if (!flag) {
			matrixStack.translate(0.0D, -0.3125D, 0.0D);
		}

		boolean flag1 = this.updateCounter / 6 % 2 == 0;
		float f1 = 0.6666667F;
		matrixStack.pushPose();
		matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		MultiBufferSource.BufferSource irendertypebuffer$impl = this.minecraft.renderBuffers().bufferSource();
		Material rendermaterial = SignRenderer.getMaterial(blockstate.getBlock());
		VertexConsumer ivertexbuilder = rendermaterial.getBuilder(irendertypebuffer$impl, this.signModel::getRenderType);
		this.signModel.root.render(matrixStack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY);
		if (flag) {
			this.signModel.stick.render(matrixStack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY);
		}

		matrixStack.popPose();
		float f2 = 0.010416667F;
		matrixStack.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);
		matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
		int i = EntityMelonGolem.te.getColor().getTextColor();
		int j = this.textInputUtil.getCursorPos();
		int k = this.textInputUtil.getSelectionPos();
		int l = this.editLine * 10 - 4 * 5;
		Matrix4f matrix4f = matrixStack.last().pose();

		for (int i1 = 0; i1 < 4; ++i1) {
			String s = this.golem.getSignText(i1).getString();
			if (s != null) {
				if (this.font.isBidirectional()) {
					s = this.font.bidirectionalShaping(s);
				}

				float f3 = (float) (-this.minecraft.font.width(s) / 2);
				this.minecraft.font.drawInBatch(s, f3, (float) (i1 * 10 - 4 * 5), i, false, matrix4f, irendertypebuffer$impl, false, 0, 15728880, false);
				if (i1 == this.editLine && j >= 0 && flag1) {
					int j1 = this.minecraft.font.width(s.substring(0, Math.max(Math.min(j, s.length()), 0)));
					int k1 = j1 - this.minecraft.font.width(s) / 2;
					if (j >= s.length()) {
						this.minecraft.font.drawInBatch("_", (float) k1, (float) l, i, false, matrix4f, irendertypebuffer$impl, false, 0, 15728880, false);
					}
				}
			}
		}

		irendertypebuffer$impl.endBatch();

		for (int i3 = 0; i3 < 4; ++i3) {
			String s1 = golem.getSignText(i3).getString();
			if (s1 != null && i3 == this.editLine && j >= 0) {
				int j3 = this.minecraft.font.width(s1.substring(0, Math.max(Math.min(j, s1.length()), 0)));
				int k3 = j3 - this.minecraft.font.width(s1) / 2;
				if (flag1 && j < s1.length()) {
					fill(matrixStack, k3, l - 1, k3 + 1, l + 9, -16777216 | i);
				}

				if (k != j) {
					int l3 = Math.min(j, k);
					int l1 = Math.max(j, k);
					int i2 = this.minecraft.font.width(s1.substring(0, l3)) - this.minecraft.font.width(s1) / 2;
					int j2 = this.minecraft.font.width(s1.substring(0, l1)) - this.minecraft.font.width(s1) / 2;
					int k2 = Math.min(i2, j2);
					int l2 = Math.max(i2, j2);
					Tesselator tessellator = Tesselator.getInstance();
					BufferBuilder bufferbuilder = tessellator.getBuilder();
					RenderSystem.disableTexture();
					RenderSystem.enableColorLogicOp();
					RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
					bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
					bufferbuilder.vertex(matrix4f, (float) k2, (float) (l + 9), 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.vertex(matrix4f, (float) l2, (float) (l + 9), 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.vertex(matrix4f, (float) l2, (float) l, 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.vertex(matrix4f, (float) k2, (float) l, 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.end();
					BufferUploader.end(bufferbuilder);
					RenderSystem.disableColorLogicOp();
					RenderSystem.enableTexture();
				}
			}
		}

		matrixStack.popPose();
		Lighting.setupFor3DItems();
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}


}

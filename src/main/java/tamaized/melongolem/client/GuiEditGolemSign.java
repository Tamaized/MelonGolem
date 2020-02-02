package tamaized.melongolem.client;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.network.server.ServerPacketHandlerMelonSign;

import java.util.List;
import java.util.Objects;

public class GuiEditGolemSign extends Screen {

	private final SignTileEntityRenderer.SignModel field_228191_a_ = new SignTileEntityRenderer.SignModel();
	private final IModProxy.ISignHolder golem;
	private int updateCounter;
	private int editLine;
	private boolean canSend = true;
	private TextInputUtil textInputUtil;

	public GuiEditGolemSign(IModProxy.ISignHolder golem) {
		super(new TranslationTextComponent("melongolemsignholder"));
		this.golem = golem;
	}

	@Override
	protected void init() {
		Objects.requireNonNull(minecraft).keyboardListener.enableRepeatEvents(true);
		this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120, 200, 20, I18n.format("gui.done"), (p_214266_1_) -> {
			this.close();
		}));
		this.textInputUtil = new TextInputUtil(this.minecraft, () -> golem.getSignText(this.editLine).getString(), (p_214265_1_) -> golem.setSignText(this.editLine, new StringTextComponent(p_214265_1_)), 90);
	}

	@Override
	public void removed() {
		if (minecraft == null)
			return;
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
		this.textInputUtil.func_216894_a(typedChar);
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
			this.textInputUtil.func_216899_b();
			return true;
		} else if (p_keyPressed_1_ != 264 && p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
			return this.textInputUtil.func_216897_a(p_keyPressed_1_) || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
		} else {
			this.editLine = this.editLine + 1 & 3;
			this.textInputUtil.func_216899_b();
			return true;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		for (int index = 0; index < 4; index++)
			EntityMelonGolem.te.setText(index, golem.getSignText(index));
		EntityMelonGolem.SIGN_TILE_BLOCKSTATE = ((WallOrFloorItem) golem.getHead().getItem()).wallBlock.getDefaultState();
		//		RenderHelper.disableGuiDepthLighting();
		this.renderBackground();
		this.drawCenteredString(this.font, I18n.format("sign.edit"), this.width / 2, 40, 0xFFFFFF);
		MatrixStack matrixstack = new MatrixStack();
		matrixstack.push();
		matrixstack.translate(this.width / 2, 0.0D, 50.0D);
		matrixstack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180F));
		matrixstack.scale(-93.75F, -93.75F, -93.75F);
		matrixstack.translate(0.0D, -1.625D, 0.0D);

		boolean flag1 = this.updateCounter / 6 % 2 == 0;
		matrixstack.push();
		matrixstack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		IRenderTypeBuffer.Impl irendertypebuffer$impl = this.minecraft.getBufferBuilders().getEntityVertexConsumers();
		Material material = SignTileEntityRenderer.getModelTexture(EntityMelonGolem.te.getBlockState().getBlock());
		IVertexBuilder ivertexbuilder = material.getVertexConsumer(irendertypebuffer$impl, this.field_228191_a_::getLayer);
		this.field_228191_a_.field_78166_a.render(matrixstack, ivertexbuilder, 0xF000F0, OverlayTexture.DEFAULT_UV);

		matrixstack.pop();
		matrixstack.translate(0.0D, (double) 0.33333334F, (double) 0.046666667F);
		matrixstack.scale(0.010416667F, -0.010416667F, 0.010416667F);
		int i = EntityMelonGolem.te.getTextColor().getTextColor();
		String[] astring = new String[4];

		for (int j = 0; j < astring.length; ++j) {
			astring[j] = EntityMelonGolem.te.getRenderText(j, (p_228192_1_) -> {
				List<ITextComponent> list = RenderComponentsUtil.splitText(p_228192_1_, 90, this.minecraft.fontRenderer, false, true);
				return list.isEmpty() ? "" : list.get(0).getFormattedText();
			});
		}

		Matrix4f matrix4f = matrixstack.peek().getModel();
		int k = this.textInputUtil.func_216896_c();
		int l = this.textInputUtil.func_216898_d();
		int i1 = this.minecraft.fontRenderer.getBidiFlag() ? -1 : 1;
		int j1 = this.editLine * 10 - EntityMelonGolem.te.signText.length * 5;

		for (int k1 = 0; k1 < astring.length; ++k1) {
			String s = astring[k1];
			if (s != null) {
				float f3 = (float) (-this.minecraft.fontRenderer.getStringWidth(s) / 2);
				this.minecraft.fontRenderer.draw(s, f3, (float) (k1 * 10 - EntityMelonGolem.te.signText.length * 5), i, false, matrix4f, irendertypebuffer$impl, false, 0, 15728880);
				if (k1 == this.editLine && k >= 0 && flag1) {
					int l1 = this.minecraft.fontRenderer.getStringWidth(s.substring(0, Math.max(Math.min(k, s.length()), 0)));
					int i2 = (l1 - this.minecraft.fontRenderer.getStringWidth(s) / 2) * i1;
					if (k >= s.length()) {
						this.minecraft.fontRenderer.draw("_", (float) i2, (float) j1, i, false, matrix4f, irendertypebuffer$impl, false, 0, 15728880);
					}
				}
			}
		}

		irendertypebuffer$impl.draw();

		for (int k3 = 0; k3 < astring.length; ++k3) {
			String s1 = astring[k3];
			if (s1 != null && k3 == this.editLine && k >= 0) {
				int l3 = this.minecraft.fontRenderer.getStringWidth(s1.substring(0, Math.max(Math.min(k, s1.length()), 0)));
				int i4 = (l3 - this.minecraft.fontRenderer.getStringWidth(s1) / 2) * i1;
				if (flag1 && k < s1.length()) {
					fill(matrix4f, i4, j1 - 1, i4 + 1, j1 + 9, -16777216 | i);
				}

				if (l != k) {
					int j4 = Math.min(k, l);
					int j2 = Math.max(k, l);
					int k2 = (this.minecraft.fontRenderer.getStringWidth(s1.substring(0, j4)) - this.minecraft.fontRenderer.getStringWidth(s1) / 2) * i1;
					int l2 = (this.minecraft.fontRenderer.getStringWidth(s1.substring(0, j2)) - this.minecraft.fontRenderer.getStringWidth(s1) / 2) * i1;
					int i3 = Math.min(k2, l2);
					int j3 = Math.max(k2, l2);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferbuilder = tessellator.getBuffer();
					RenderSystem.disableTexture();
					RenderSystem.enableColorLogicOp();
					RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
					bufferbuilder.vertex(matrix4f, (float) i3, (float) (j1 + 9), 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.vertex(matrix4f, (float) j3, (float) (j1 + 9), 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.vertex(matrix4f, (float) j3, (float) j1, 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.vertex(matrix4f, (float) i3, (float) j1, 0.0F).color(0, 0, 255, 255).endVertex();
					bufferbuilder.finishDrawing();
					WorldVertexBufferUploader.draw(bufferbuilder);
					RenderSystem.disableColorLogicOp();
					RenderSystem.enableTexture();
				}
			}
		}

		matrixstack.pop();
		//		RenderHelper.enableGuiDepthLighting();
		super.render(mouseX, mouseY, partialTicks);

		/*EntityMelonGolem.te.func_214062_a(this.editLine, this.textInputUtil.func_216896_c(), this.textInputUtil.func_216898_d(), flag1);
		for (int index = 0; index < 4; index++)
			EntityMelonGolem.te.setText(index, golem.getSignText(index));
		TileEntityRendererDispatcher.instance.getRenderer(SignTileEntity.class).render(EntityMelonGolem.te, -0.5D, -0.75D, -0.5D, 0.0F, -1);
		EntityMelonGolem.te.func_214063_g();
		GlStateManager.popMatrix();
		super.render(mouseX, mouseY, partialTicks);*/
	}


}

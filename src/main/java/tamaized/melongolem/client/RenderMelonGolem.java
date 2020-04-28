package tamaized.melongolem.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.SnowManModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import javax.annotation.Nonnull;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class RenderMelonGolem<T extends MobEntity & IModProxy.ISignHolder> extends MobRenderer<T, SnowManModel<T>> {
	private static final ResourceLocation TEXTURES = new ResourceLocation(MelonMod.MODID, "textures/entity/golem.png");
	private static final ResourceLocation TEXTURES_GREY = new ResourceLocation(MelonMod.MODID, "textures/entity/greygolem.png");
	private static final ResourceLocation TEXTURES_GLISTER = new ResourceLocation(MelonMod.MODID, "textures/entity/glistening_melon_golem.png");
	private static final ResourceLocation TEXTURES_GLISTER_OVERLAY = new ResourceLocation(MelonMod.MODID, "textures/entity/glistening_melon_golem_overlay.png");
	private static final ColorHack COLOR_STATE = new ColorHack();
	private final Type type;

	@SuppressWarnings("unchecked")
	public RenderMelonGolem(EntityRendererManager renderManagerIn, Type type) {
		super(renderManagerIn, new SnowManModel() {
			@Override
			public void render(@Nonnull MatrixStack stack, @Nonnull IVertexBuilder buffer, int p_225598_3_, int p_225598_4_, float red, float green, float blue, float alpha) {
				super.render(stack, buffer, p_225598_3_, p_225598_4_, COLOR_STATE.red, COLOR_STATE.green, COLOR_STATE.blue, alpha);
			}
		}, type == Type.TINY ? 0.125F : 0.5F);
		addLayer(new LayerMelonHead(this));
		if (type == Type.GLISTER)
			addLayer(new LayerMelonGlister(this));
		this.type = type;
	}

	@Override
	public void render(T entity, float rotation, float partialTicks, @Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int light) {
		stack.push();
		if (type == Type.TINY) {
			EntityTinyMelonGolem golem = (EntityTinyMelonGolem) entity;
			if (golem.isEnabled()) {
				int color = golem.getColor();
				COLOR_STATE.red = ((color >> 16) & 0xFF) / 255F;
				COLOR_STATE.green = ((color >> 8) & 0xFF) / 255F;
				COLOR_STATE.blue = ((color) & 0xFF) / 255F;
			}
		}
		super.render(entity, rotation, partialTicks, stack, buffer, light);
		stack.pop();
	}

	@Nonnull
	@Override
	public ResourceLocation getEntityTexture(@Nonnull T entity) {
		return entity instanceof EntityTinyMelonGolem && ((EntityTinyMelonGolem) entity).isEnabled() ? TEXTURES_GREY : type == Type.GLISTER ? TEXTURES_GLISTER : TEXTURES;
	}

	@Override
	protected void preRenderCallback(T p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
		if (type == Type.TINY)
			p_225620_2_.scale(0.25F, 0.25F, 0.25F);
	}

	public enum Type {
		NORMAL, TINY, GLISTER
	}

	private static class ColorHack {
		private float red = 1F;
		private float green = 1F;
		private float blue = 1F;
		private float alpha = 1F;
	}

	public static class Factory {

		public static RenderMelonGolem normal(EntityRendererManager renderManager) {
			return new RenderMelonGolem(renderManager, Type.NORMAL);
		}

		public static RenderMelonGolem tiny(EntityRendererManager renderManager) {
			return new RenderMelonGolem(renderManager, Type.TINY);
		}

		public static RenderMelonGolem glister(EntityRendererManager renderManager) {
			return new RenderMelonGolem(renderManager, Type.GLISTER);
		}

	}

	class LayerMelonGlister<E extends T> extends LayerRenderer<E, SnowManModel<E>> {

		public LayerMelonGlister(IEntityRenderer<E, SnowManModel<E>> p_i50926_1_) {
			super(p_i50926_1_);
		}

		@Override
		public void render(@Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int light, @Nonnull E entity, float limbSwing, float limbSwingAmount, float partialTicks, float rotation, float yawHead, float pitch) {
			IVertexBuilder builder = buffer.getBuffer(RenderType.getEnergySwirl(TEXTURES_GLISTER_OVERLAY, 0, 0));
			//			GlStateManager.enableBlend();
			//			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			stack.push();
			final float s = 1.01F;
			stack.scale(s, s, s);
			//			int i = 0xF000F0;
			//			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, i % 65536, i >> 16);
			getEntityModel().render(stack, builder, 0xF000F0, getPackedOverlay(entity, getOverlayProgress(entity, partialTicks)), 1F, 1F, 1F, (!isVisible(entity) && !entity.isInvisibleToPlayer(Objects.requireNonNull(Minecraft.getInstance().player))) ? 0.15F : 1.0F);
			stack.pop();
			//			GlStateManager.disableBlend();
		}
	}
}
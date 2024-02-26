package tamaized.melongolem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityGlisteringMelonGolem;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import javax.annotation.Nonnull;
import java.util.Objects;

public class RenderMelonGolem<T extends Mob & ISignHolder> extends MobRenderer<T, SnowGolemModel<T>> {
	private static final ResourceLocation TEXTURES = new ResourceLocation(MelonMod.MODID, "textures/entity/golem.png");
	private static final ResourceLocation TEXTURES_GREY = new ResourceLocation(MelonMod.MODID, "textures/entity/greygolem.png");
	private static final ResourceLocation TEXTURES_GLISTER = new ResourceLocation(MelonMod.MODID, "textures/entity/glistening_melon_golem.png");
	private static final ResourceLocation TEXTURES_GLISTER_OVERLAY = new ResourceLocation(MelonMod.MODID, "textures/entity/glistening_melon_golem_overlay.png");
	private static final ColorHack COLOR_STATE = new ColorHack();
	private final Type type;

	public RenderMelonGolem(EntityRendererProvider.Context renderManagerIn, Type type) {
		super(renderManagerIn, new SnowGolemModel<>(renderManagerIn.bakeLayer(ModelLayers.SNOW_GOLEM)) {
			@Override
			public void renderToBuffer(@Nonnull PoseStack stack, @Nonnull VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
				super.renderToBuffer(stack, buffer, light, overlay, COLOR_STATE.red, COLOR_STATE.green, COLOR_STATE.blue, alpha);
			}
		}, type == Type.TINY ? 0.125F : 0.5F);
		addLayer(new LayerMelonHead<>(this));
		if (type == Type.GLISTER)
			addLayer(new LayerMelonGlister<>(this));
		this.type = type;
	}

	@Override
	public void render(T entity, float rotation, float partialTicks, @Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer, int light) {
		stack.pushPose();
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
		stack.popPose();
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull T entity) {
		return entity instanceof EntityTinyMelonGolem && ((EntityTinyMelonGolem) entity).isEnabled() ? TEXTURES_GREY : type == Type.GLISTER ? TEXTURES_GLISTER : TEXTURES;
	}

	@Override
	protected void scale(T entity, PoseStack stack, float partialTicks) {
		if (type == Type.TINY)
			stack.scale(0.25F, 0.25F, 0.25F);
	}

	public enum Type {
		NORMAL, TINY, GLISTER
	}

	private static class ColorHack {
		private float red = 1F;
		private float green = 1F;
		private float blue = 1F;
	}

	public static class Factory {

		public static RenderMelonGolem<EntityMelonGolem> normal(EntityRendererProvider.Context renderManager) {
			return new RenderMelonGolem<>(renderManager, Type.NORMAL);
		}

		public static RenderMelonGolem<EntityTinyMelonGolem> tiny(EntityRendererProvider.Context renderManager) {
			return new RenderMelonGolem<>(renderManager, Type.TINY);
		}

		public static RenderMelonGolem<EntityGlisteringMelonGolem> glister(EntityRendererProvider.Context renderManager) {
			return new RenderMelonGolem<>(renderManager, Type.GLISTER);
		}

	}

	class LayerMelonGlister<E extends T> extends RenderLayer<E, SnowGolemModel<E>> {

		public LayerMelonGlister(RenderLayerParent<E, SnowGolemModel<E>> p_i50926_1_) {
			super(p_i50926_1_);
		}

		@Override
		public void render(@Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer, int light, @Nonnull E entity, float limbSwing, float limbSwingAmount, float partialTicks, float rotation, float yawHead, float pitch) {
			VertexConsumer builder = buffer.getBuffer(RenderType.energySwirl(TEXTURES_GLISTER_OVERLAY, 0, 0));
			stack.pushPose();
			final float s = 1.01F;
			stack.scale(s, s, s);
			getParentModel().renderToBuffer(stack, builder, 0xF000F0, getOverlayCoords(entity, getWhiteOverlayProgress(entity, partialTicks)), 1F, 1F, 1F, (!isBodyVisible(entity) && !entity.isInvisibleTo(Objects.requireNonNull(Minecraft.getInstance().player))) ? 0.15F : 1.0F);
			stack.popPose();
		}
	}
}
package tamaized.melongolem.client;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
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

@OnlyIn(Dist.CLIENT)
public class RenderMelonGolem<T extends MobEntity & IModProxy.ISignHolder> extends MobRenderer<T, SnowManModel<T>> {
	private static final ResourceLocation TEXTURES = new ResourceLocation(MelonMod.MODID, "textures/entity/golem.png");
	private static final ResourceLocation TEXTURES_GREY = new ResourceLocation(MelonMod.MODID, "textures/entity/greygolem.png");
	private static final ResourceLocation TEXTURES_GLISTER = new ResourceLocation(MelonMod.MODID, "textures/entity/glistening_melon_golem.png");
	private static final ResourceLocation TEXTURES_GLISTER_OVERLAY = new ResourceLocation(MelonMod.MODID, "textures/entity/glistening_melon_golem_overlay.png");

	private final Type type;

	@SuppressWarnings("unchecked")
	public RenderMelonGolem(EntityRendererManager renderManagerIn, Type type) {
		super(renderManagerIn, new SnowManModel(), type == Type.TINY ? 0.125F : 0.5F);
		addLayer(new LayerMelonHead(this));
		if(type == Type.GLISTER)
			addLayer(new LayerMelonGlister(this));
		this.type = type;
	}

	class LayerMelonGlister<E extends MobEntity> extends LayerRenderer<E, SnowManModel<E>> {

		public LayerMelonGlister(IEntityRenderer<E, SnowManModel<E>> p_i50926_1_) {
			super(p_i50926_1_);
		}

		@Override
		public void render(@Nonnull E entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			bindTexture(TEXTURES_GLISTER_OVERLAY);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			GlStateManager.pushMatrix();
			final float s = 1.01F;
			GlStateManager.scalef(s, s, s);
			int i = 0xF000F0;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, i % 65536, i >> 16);
			getEntityModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			GlStateManager.popMatrix();
			GlStateManager.disableBlend();
		}

		@Override
		public boolean shouldCombineTextures() {
			return true;
		}
	}

	@Override
	public void doRender(@Nonnull T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		if (type == Type.TINY) {
			GlStateManager.translatef(0, -1.11F, 0);
			EntityTinyMelonGolem golem = (EntityTinyMelonGolem) entity;
			if (golem.isEnabled()) {
				int color = golem.getColor();
				float r = ((color >> 16) & 0xFF) / 255F;
				float g = ((color >> 8) & 0xFF) / 255F;
				float b = ((color) & 0xFF) / 255F;
				GlStateManager.color4f(r, g, b, 1F);
			}
		}
		if(type == Type.GLISTER){
			int i = entity.world.getCombinedLight(entity.getPosition(), 0) | 0x100010;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, i % 65536, i >> 16);
		}
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull T entity) {
		return entity instanceof EntityTinyMelonGolem && ((EntityTinyMelonGolem) entity).isEnabled() ? TEXTURES_GREY : type == Type.GLISTER ? TEXTURES_GLISTER : TEXTURES;
	}

	@Override
	public float prepareScale(@Nonnull T entitylivingbaseIn, float partialTicks) {
		return super.prepareScale(entitylivingbaseIn, partialTicks) * (type == Type.TINY ? 0.25F : 1F);
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

	public enum Type {
		NORMAL, TINY, GLISTER
	}
}
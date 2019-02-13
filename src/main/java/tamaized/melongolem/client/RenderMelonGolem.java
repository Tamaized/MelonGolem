package tamaized.melongolem.client;

import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.MelonMod;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderMelonGolem<T extends EntityLiving & IModProxy.ISignHolder> extends RenderLiving<T> {
	private static final ResourceLocation TEXTURES = new ResourceLocation(MelonMod.MODID, "textures/entity/golem.png");

	private final boolean tiny;

	@SuppressWarnings("unchecked")
	public RenderMelonGolem(RenderManager renderManagerIn, boolean tiny) {
		super(renderManagerIn, new ModelSnowMan(), tiny ? 0.125F : 0.5F);
		addLayer(new LayerMelonHead(this));
		this.tiny = tiny;
	}

	@Override
	public void doRender(@Nonnull T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		if (tiny)
			GlStateManager.translate(0, -1.11F, 0);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull T entity) {
		return TEXTURES;
	}

	@Override
	public float prepareScale(@Nonnull T entitylivingbaseIn, float partialTicks) {
		return super.prepareScale(entitylivingbaseIn, partialTicks) * (tiny ? 0.25F : 1F);
	}

	public static class Factory {

		public static RenderMelonGolem normal(RenderManager renderManager) {
			return new RenderMelonGolem(renderManager, false);
		}

		public static RenderMelonGolem tiny(RenderManager renderManager) {
			return new RenderMelonGolem(renderManager, true);
		}

	}
}
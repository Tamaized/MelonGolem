package tamaized.melongolem.client;


import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamaized.melongolem.common.EntityMelonSlice;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class RenderMelonSlice extends EntityRenderer<EntityMelonSlice> {

	public RenderMelonSlice(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void doRender(@Nonnull EntityMelonSlice entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float) x, (float) y, (float) z);
		GlStateManager.enableRescaleNormal();
		GlStateManager.rotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float) (renderManager.options.thirdPersonView == 2 ? -1 : 1) * renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

		if (renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(getTeamColor(entity));
		}

		Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(entity.isGlistering() ? Items.GLISTERING_MELON_SLICE : Items.MELON_SLICE), net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.GROUND);

		if (renderOutlines) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityMelonSlice entity) {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}
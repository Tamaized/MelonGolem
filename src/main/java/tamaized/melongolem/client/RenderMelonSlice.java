package tamaized.melongolem.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamaized.melongolem.common.EntityMelonSlice;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class RenderMelonSlice extends Render<EntityMelonSlice> {

	public RenderMelonSlice(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void doRender(@Nonnull EntityMelonSlice entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float) x, (float) y, (float) z);
		GlStateManager.enableRescaleNormal();
		GlStateManager.rotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float) (renderManager.options.thirdPersonView == 2 ? -1 : 1) * renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if (renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(getTeamColor(entity));
		}

		Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(Items.MELON_SLICE), ItemCameraTransforms.TransformType.GROUND);

		if (renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityMelonSlice entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
}
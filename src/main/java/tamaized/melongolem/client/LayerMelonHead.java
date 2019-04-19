package tamaized.melongolem.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.ModelSnowMan;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.common.EntityMelonGolem;

import javax.annotation.Nonnull;

public class LayerMelonHead<T extends EntityLiving & IModProxy.ISignHolder> implements LayerRenderer<T> {
	private final RenderMelonGolem renderer;

	public LayerMelonHead(RenderMelonGolem render) {
		renderer = render;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void render(@Nonnull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		ItemStack stack = entity.getHead();
		if (!entity.isInvisible() || !stack.isEmpty()) {
			GlStateManager.pushMatrix();
			((ModelSnowMan) renderer.getMainModel()).head.postRender(scale);
			GlStateManager.translatef(0.0F, /*-0.34375F*/-scale / (2F / 11F), 0.0F);
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			final float s = scale * 10F;
			GlStateManager.scalef(s, -s, -s);
			if (stack.getItem() == Items.SIGN) {
				for (int index = 0; index < 4; index++)
					EntityMelonGolem.te.func_212365_a(index, entity.getSignText(index));
				GlStateManager.pushMatrix();
				TileEntityRendererDispatcher.instance.getRenderer(TileEntitySign.class).render(EntityMelonGolem.te, -0.5F, -0.5F, -1.325F, 1, -1);
				GlStateManager.popMatrix();
			} else
				Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.HEAD);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return true;
	}
}

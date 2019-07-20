package tamaized.melongolem.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.SnowManModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.SignTileEntity;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.common.EntityMelonGolem;

import javax.annotation.Nonnull;

public class LayerMelonHead<T extends LivingEntity & IModProxy.ISignHolder> extends LayerRenderer<T, SnowManModel<T>> {

	public LayerMelonHead(IEntityRenderer<T, SnowManModel<T>> p_i50926_1_) {
		super(p_i50926_1_);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void render(@Nonnull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		ItemStack stack = entity.getHead();
		if (!entity.isInvisible() || !stack.isEmpty()) {
			GlStateManager.pushMatrix();
			getEntityModel().head.postRender(scale);
			GlStateManager.translatef(0.0F, /*-0.34375F*/-scale / (2F / 11F), 0.0F);
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			final float s = scale * 10F;
			GlStateManager.scalef(s, -s, -s);
			if (stack.getItem() == Items.OAK_SIGN) {
				for (int index = 0; index < 4; index++)
					EntityMelonGolem.te.setText(index, entity.getSignText(index));
				GlStateManager.pushMatrix();
				TileEntityRendererDispatcher.instance.getRenderer(SignTileEntity.class).render(EntityMelonGolem.te, -0.5F, -0.5F, -1.325F, 1, -1);
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

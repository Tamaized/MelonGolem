package tamaized.melongolem.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
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
import java.util.Objects;

public class LayerMelonHead<T extends LivingEntity & IModProxy.ISignHolder> extends LayerRenderer<T, SnowManModel<T>> {

	public LayerMelonHead(IEntityRenderer<T, SnowManModel<T>> p_i50926_1_) {
		super(p_i50926_1_);
	}

	@Override
	public void render(@Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int light, @Nonnull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float rotation, float yawHead, float pitch) {
		ItemStack itemStack = entity.getHead();
		if (!entity.isInvisible() || !itemStack.isEmpty()) {
			stack.push();
//			getEntityModel().head.postRender(scale);
//			GlStateManager.translatef(0.0F, /*-0.34375F*/-scale / (2F / 11F), 0.0F);
//			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
//			final float s = scale * 10F;
//			GlStateManager.scalef(s, -s, -s);
			if (itemStack.getItem() == Items.OAK_SIGN) {
				for (int index = 0; index < 4; index++)
					EntityMelonGolem.te.setText(index, entity.getSignText(index));
				stack.push();
				Objects.requireNonNull(TileEntityRendererDispatcher.instance.getRenderer(EntityMelonGolem.te)).render(EntityMelonGolem.te, partialTicks, stack, buffer, light, LivingRenderer.getOverlay(entity, 0F));
				stack.pop();
			} else
				Minecraft.getInstance().getItemRenderer().renderItem(itemStack, ItemCameraTransforms.TransformType.HEAD, light, LivingRenderer.getOverlay(entity, 0F), stack, buffer);
			stack.pop();
		}
	}
}

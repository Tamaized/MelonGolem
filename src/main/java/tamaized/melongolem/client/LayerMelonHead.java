package tamaized.melongolem.client;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.entity.model.SnowManModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.tileentity.SignTileEntity;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityMelonGolem;

import javax.annotation.Nonnull;
import java.util.Objects;

public class LayerMelonHead<T extends LivingEntity & IModProxy.ISignHolder> extends LayerRenderer<T, SnowManModel<T>> {

	public LayerMelonHead(IEntityRenderer<T, SnowManModel<T>> p_i50926_1_) {
		super(p_i50926_1_);
	}

	@Override
	public void render(@Nonnull MatrixStack stack, @Nonnull IRenderTypeBuffer buffer, int light, @Nonnull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float age, float yawHead, float pitch) {
		ItemStack itemStack = entity.getHead();
		if (!entity.isInvisible() || !itemStack.isEmpty()) {
			stack.push();
			getEntityModel().head.translateRotate(stack);
			stack.translate(0.0D, -0.25D, 0.0D);
			stack.rotate(Vector3f.YP.rotationDegrees(180F));
			stack.scale(0.625F, -0.625F, -0.625F);
			if (MelonMod.SIGNS.contains(itemStack.getItem())) {
				for (int index = 0; index < 4; index++)
					EntityMelonGolem.te.setText(index, entity.getSignText(index));
				stack.translate(-0.5D, -0.5D, -1.33D);
				EntityMelonGolem.SIGN_TILE_BLOCKSTATE = ((WallOrFloorItem)entity.getHead().getItem()).wallBlock.getDefaultState();
				Objects.requireNonNull(TileEntityRendererDispatcher.instance.getRenderer(EntityMelonGolem.te)).render(EntityMelonGolem.te, partialTicks, stack, buffer, light, LivingRenderer.getPackedOverlay(entity, 0F));
			} else
				Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entity, itemStack, ItemCameraTransforms.TransformType.HEAD, false, stack, buffer, light);
			stack.pop();
		}
	}
}

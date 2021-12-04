package tamaized.melongolem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityMelonGolem;

import javax.annotation.Nonnull;
import java.util.Objects;

public class LayerMelonHead<T extends LivingEntity & IModProxy.ISignHolder> extends RenderLayer<T, SnowGolemModel<T>> {

	public LayerMelonHead(RenderLayerParent<T, SnowGolemModel<T>> p_i50926_1_) {
		super(p_i50926_1_);
	}

	@Override
	public void render(@Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer, int light, @Nonnull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float age, float yawHead, float pitch) {
		ItemStack itemStack = entity.getHead();
		if (!entity.isInvisible() || !itemStack.isEmpty()) {
			stack.pushPose();
			getParentModel().head.translateAndRotate(stack);
			stack.translate(0.0D, -0.25D, 0.0D);
			stack.mulPose(Vector3f.YP.rotationDegrees(180F));
			stack.scale(0.625F, -0.625F, -0.625F);
			if (MelonMod.SIGNS.contains(itemStack.getItem())) {
				for (int index = 0; index < 4; index++)
					EntityMelonGolem.te.setMessage(index, entity.getSignText(index));
				stack.translate(-0.5D, -0.5D, -1.33D);
				EntityMelonGolem.SIGN_TILE_BLOCKSTATE = ((StandingAndWallBlockItem)entity.getHead().getItem()).wallBlock.defaultBlockState();
				Objects.requireNonNull(Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(EntityMelonGolem.te)).render(EntityMelonGolem.te, partialTicks, stack, buffer, light, LivingEntityRenderer.getOverlayCoords(entity, 0F));
			} else
				Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, itemStack, ItemTransforms.TransformType.HEAD, false, stack, buffer, light);
			stack.popPose();
		}
	}
}

package tamaized.melongolem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.entity.SignText;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.common.EntityMelonGolem;

import javax.annotation.Nonnull;
import java.util.Objects;

public class LayerMelonHead<T extends LivingEntity & ISignHolder> extends RenderLayer<T, SnowGolemModel<T>> {

	public LayerMelonHead(RenderLayerParent<T, SnowGolemModel<T>> parent) {
		super(parent);
	}

	@Override
	public void render(@Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer, int light, @Nonnull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float age, float yawHead, float pitch) {
		ItemStack itemStack = entity.getHead();
		if (!entity.isInvisible() || !itemStack.isEmpty()) {
			stack.pushPose();
			getParentModel().head.translateAndRotate(stack);
			stack.translate(0.0D, -0.25D, 0.0D);
			stack.mulPose(Axis.YP.rotationDegrees(180F));
			stack.scale(0.625F, -0.625F, -0.625F);
			if (itemStack.is(ItemTags.SIGNS) && itemStack.getItem() instanceof StandingAndWallBlockItem item) {
				Component[] signText = new Component[4];
				for (int index = 0; index < 4; index++)
					signText[index] = entity.getSignText(index);
				stack.translate(-0.5D, -0.5D, -1.33D);
				//we have to set these like this because using the setter methods calls markUpdated, which crashes because our TE has no level
				//using te.getFrontText().whatever doesnt work either for some reason
				EntityMelonGolem.te.frontText = new SignText(signText, signText, entity.getTextColor(), entity.glowingText());
				EntityMelonGolem.SIGN_TILE_BLOCKSTATE = item.wallBlock.defaultBlockState();
				Objects.requireNonNull(Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(EntityMelonGolem.te)).render(EntityMelonGolem.te, partialTicks, stack, buffer, light, LivingEntityRenderer.getOverlayCoords(entity, 0F));
			} else
				Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.HEAD, light, OverlayTexture.NO_OVERLAY, stack, buffer, null, 0);
			stack.popPose();
		}
	}
}

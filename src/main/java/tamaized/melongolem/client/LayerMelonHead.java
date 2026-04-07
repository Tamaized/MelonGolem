package tamaized.melongolem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.animal.golem.SnowGolemModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.entity.SignText;
import tamaized.melongolem.common.EntityMelonGolem;

import javax.annotation.Nonnull;
import java.util.Objects;

public class LayerMelonHead extends RenderLayer<MelonGolemRenderState, SnowGolemModel> {

	public LayerMelonHead(RenderLayerParent<MelonGolemRenderState, SnowGolemModel> parent) {
		super(parent);
	}

	@Override
	public void submit(@Nonnull PoseStack stack, @Nonnull SubmitNodeCollector buffer, int light, @Nonnull MelonGolemRenderState entity, float yawHead, float pitch) {
		ItemStack itemStack = entity.head;
		if (!entity.isInvisible || !itemStack.isEmpty()) {
			stack.pushPose();
			getParentModel().head.translateAndRotate(stack);
			stack.translate(0.0D, -0.25D, 0.0D);
			stack.mulPose(Axis.YP.rotationDegrees(180F));
			stack.scale(0.625F, -0.625F, -0.625F);
			if (itemStack.is(ItemTags.SIGNS) && itemStack.getItem() instanceof StandingAndWallBlockItem item) {
				Component[] signText = new Component[4];
				for (int index = 0; index < 4; index++)
					signText[index] = entity.signText.get(index);
				stack.translate(-0.5D, -0.5D, -1.33D);
				//we have to set these like this because using the setter methods calls markUpdated, which crashes because our TE has no level
				//using te.getFrontText().whatever doesnt work either for some reason
				EntityMelonGolem.te.frontText = new SignText(signText, signText, entity.textColor, entity.isTextGlowing);
				EntityMelonGolem.SIGN_TILE_BLOCKSTATE = item.wallBlock.defaultBlockState();
				BlockEntityRenderState teState = Minecraft.getInstance().getBlockEntityRenderDispatcher().tryExtractRenderState(EntityMelonGolem.te, entity.partialTick, null, null);

				if (teState instanceof SignRenderState signState) {
					Objects.requireNonNull(Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(EntityMelonGolem.te)).submit(signState, stack, buffer, Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState);
				}
			} else
				entity.headItem.submit(stack, buffer, light, OverlayTexture.NO_OVERLAY, entity.outlineColor);
			stack.popPose();
		}
	}
}

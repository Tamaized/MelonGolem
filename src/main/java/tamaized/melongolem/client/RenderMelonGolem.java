package tamaized.melongolem.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.animal.golem.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityGlisteringMelonGolem;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import javax.annotation.Nonnull;

public class RenderMelonGolem<T extends Mob & ISignHolder> extends MobRenderer<T, MelonGolemRenderState, SnowGolemModel> {
	private static final Identifier TEXTURES = Identifier.fromNamespaceAndPath(MelonMod.MODID, "textures/entity/golem.png");
	private static final Identifier TEXTURES_GREY = Identifier.fromNamespaceAndPath(MelonMod.MODID, "textures/entity/greygolem.png");
	private static final Identifier TEXTURES_GLISTER = Identifier.fromNamespaceAndPath(MelonMod.MODID, "textures/entity/glistening_melon_golem.png");
	private static final Identifier TEXTURES_GLISTER_OVERLAY = Identifier.fromNamespaceAndPath(MelonMod.MODID, "textures/entity/glistening_melon_golem_overlay.png");
	private final ItemModelResolver itemModelResolver;
	private final Type type;

	public RenderMelonGolem(EntityRendererProvider.Context renderManagerIn, Type type) {
		super(renderManagerIn, new SnowGolemModel(renderManagerIn.bakeLayer(ModelLayers.SNOW_GOLEM)), type == Type.TINY ? 0.125F : 0.5F);
		addLayer(new LayerMelonHead(this));
		if (type == Type.GLISTER)
			addLayer(new LayerMelonGlister(this));
		this.itemModelResolver = renderManagerIn.getItemModelResolver();
		this.type = type;
	}

	@Override
	public MelonGolemRenderState createRenderState() {
		return new MelonGolemRenderState();
	}

	@Override
	public void extractRenderState(T entity, MelonGolemRenderState state, float partialTicks) {
		super.extractRenderState(entity, state, partialTicks);
		state.head = entity.getHead();
		state.isTinyMelon = type == Type.TINY && entity instanceof EntityTinyMelonGolem;
		state.isEnabled = entity instanceof EntityTinyMelonGolem tiny && tiny.isEnabled();
		state.color = entity instanceof EntityTinyMelonGolem tiny ? tiny.getColor() : 0xFFFFFF;
		state.textColor = entity.getTextColor();
		state.isTextGlowing = entity.glowingText();
		this.itemModelResolver.updateForLiving(state.blockState, entity.getHead(), ItemDisplayContext.HEAD, entity);
	}

	@Override
	protected int getModelTint(MelonGolemRenderState state) {
		return state.isEnabled ? state.color : super.getModelTint(state);
	}

	@Nonnull
	@Override
	public Identifier getTextureLocation(@Nonnull MelonGolemRenderState entity) {
		return entity.isTinyMelon && entity.isEnabled ? TEXTURES_GREY : type == Type.GLISTER ? TEXTURES_GLISTER : TEXTURES;
	}

	@Override
	protected void scale(MelonGolemRenderState entity, PoseStack stack) {
		if (type == Type.TINY)
			stack.scale(0.25F, 0.25F, 0.25F);
	}

	public enum Type {
		NORMAL, TINY, GLISTER
	}

	public static class Factory {

		public static RenderMelonGolem<EntityMelonGolem> normal(EntityRendererProvider.Context renderManager) {
			return new RenderMelonGolem<>(renderManager, Type.NORMAL);
		}

		public static RenderMelonGolem<EntityTinyMelonGolem> tiny(EntityRendererProvider.Context renderManager) {
			return new RenderMelonGolem<>(renderManager, Type.TINY);
		}

		public static RenderMelonGolem<EntityGlisteringMelonGolem> glister(EntityRendererProvider.Context renderManager) {
			return new RenderMelonGolem<>(renderManager, Type.GLISTER);
		}

	}

	class LayerMelonGlister extends RenderLayer<MelonGolemRenderState, SnowGolemModel> {

		public LayerMelonGlister(RenderLayerParent<MelonGolemRenderState, SnowGolemModel> p_i50926_1_) {
			super(p_i50926_1_);
		}

		@Override
		public void submit(@Nonnull PoseStack stack, @Nonnull SubmitNodeCollector buffer, int light, @Nonnull MelonGolemRenderState entity, float yawHead, float pitch) {
			stack.pushPose();
			final float s = 1.01F;
			stack.scale(s, s, s);
			buffer.order(1)
					.submitModel(
						getParentModel(),
						entity,
						stack,
						RenderTypes.energySwirl(TEXTURES_GLISTER_OVERLAY, 0, 0),
						light,
						getOverlayCoords(entity, getWhiteOverlayProgress(entity)),
						0x00FFFFFF | (!isBodyVisible(entity) && !entity.isInvisibleToPlayer ? 0x26000000 : 0xFF000000),
						null,
						entity.outlineColor,
						null);
			stack.popPose();
		}
	}
}
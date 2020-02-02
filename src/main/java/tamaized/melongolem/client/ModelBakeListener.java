package tamaized.melongolem.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tamaized.melongolem.MelonMod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber(modid = MelonMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModelBakeListener {

	@SubscribeEvent
	public static void modelBake(ModelBakeEvent event) {
		for (int i = 0; i <= 1; i++) {
			ModelResourceLocation mrl = new ModelResourceLocation(Objects.requireNonNull(MelonMod.glisteringMelonBlock.getRegistryName()), i == 0 ? "" : "inventory");
			final IBakedModel model = event.getModelRegistry().get(mrl);
			event.getModelRegistry().put(mrl, new IBakedModel() {
				private Map<Direction, List<BakedQuad>> cachedQuads = Maps.newHashMap();

				@Nonnull
				@Override
				public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
					return cachedQuads.computeIfAbsent(side, (face) -> {
						List<BakedQuad> delegateQuads = model.getQuads(state, side, rand);
						List<BakedQuad> quads = Lists.newArrayList();
						for (BakedQuad quad : delegateQuads)
							quads.add(delegateQuads.indexOf(quad) == 1 ? transformQuad(quad, 0.007F) : quad);
						return quads;
					});
				}

				@Override
				public boolean isAmbientOcclusion() {
					return model.isAmbientOcclusion();
				}

				@Override
				public boolean isGui3d() {
					return model.isGui3d();
				}

				@Override
				public boolean isSideLit() {
					return model.isSideLit();
				}

				@Override
				public boolean isBuiltInRenderer() {
					return model.isBuiltInRenderer();
				}

				@Nonnull
				@Override
				public TextureAtlasSprite getParticleTexture() {
					return model.getParticleTexture();
				}

				@Nonnull
				@Override
				public ItemOverrideList getOverrides() {
					return model.getOverrides();
				}

				@Nonnull
				@Override
				@SuppressWarnings("deprecation")
				public net.minecraft.client.renderer.model.ItemCameraTransforms getItemCameraTransforms() {
					return model.getItemCameraTransforms();
				}

			});
		}
	}

	private static BakedQuad transformQuad(BakedQuad quad, final float light) {
		BakedQuadBuilder builder = new BakedQuadBuilder();

		VertexLighterFlat trans = new VertexLighterFlat(Minecraft.getInstance().getBlockColors()) {
			@Override
			protected void updateLightmap(@Nonnull float[] normal, float[] lightmap, float x, float y, float z) {
				lightmap[0] = light;
				lightmap[1] = light;
			}

			@Override
			public void setQuadTint(int tint) {
				// NO OP
			}
		};

		trans.setParent(builder);

		quad.pipe(trans);

		builder.setQuadTint(quad.getTintIndex());
		builder.setQuadOrientation(quad.getFace());
		builder.setTexture(quad.getSprite());
		builder.setApplyDiffuseLighting(false);

		return builder.build();
	}

}

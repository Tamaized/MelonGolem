package tamaized.melongolem.client;

import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.pipeline.LightUtil;
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
			final BakedModel model = event.getModelRegistry().get(mrl);
			event.getModelRegistry().put(mrl, new BakedModel() {
				private Map<Direction, List<BakedQuad>> cachedQuads = Maps.newHashMap();

				@Nonnull
				@Override
				public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
					return cachedQuads.computeIfAbsent(side, (face) -> {
						List<BakedQuad> quads = model.getQuads(state, side, rand);
						for (BakedQuad quad : quads)
							if(quads.indexOf(quad) == 1)
								LightUtil.setLightData(quad, 0xF000F0);
						return quads;
					});
				}

				@Override
				public boolean useAmbientOcclusion() {
					return model.useAmbientOcclusion();
				}

				@Override
				public boolean isGui3d() {
					return model.isGui3d();
				}

				@Override
				public boolean usesBlockLight() {
					return model.usesBlockLight();
				}

				@Override
				public boolean isCustomRenderer() {
					return model.isCustomRenderer();
				}

				@Nonnull
				@Override
				@Deprecated
				public TextureAtlasSprite getParticleIcon() {
					return model.getParticleIcon();
				}

				@Nonnull
				@Override
				public ItemOverrides getOverrides() {
					return model.getOverrides();
				}

				@Nonnull
				@Override
				@SuppressWarnings("deprecation")
				public ItemTransforms getTransforms() {
					return model.getTransforms();
				}

			});
		}
	}

}

package tamaized.melongolem.datagen.assets.bakedmodel.block;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.datagenutil.assets.bakedmodel.block.BlockModelHolder;
import tamaized.datagenutil.assets.bakedmodel.ExtendedTextureMapping;
import tamaized.datagenutil.assets.bakedmodel.FurtherExtendedModelTemplateBuilder;
import tamaized.datagenutil.assets.bakedmodel.ModelHolder;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.datagen.assets.bakedmodel.block.overlay.OverlaySideBlockModelHolder;
import tamaized.melongolem.datagen.util.ModTextureSlots;
import tamaized.melongolem.registry.ModBlocks;

import java.util.Optional;

@Component
public class GlisteringMelonBlockModelHolder extends BlockModelHolder {

	@Autowired
	private ModBlocks blocks;

	@Autowired
	private OverlaySideBlockModelHolder overlaySideBlockModelHolder;

	@Override
	protected @Nullable DeferredHolder<Block, ? extends Block> blockForName() {
		return blocks.GLISTERING_MELON;
	}

	@Override
	public boolean hasStandardBlockItem() {
		return true;
	}

	@Override
	public Optional<ModelHolder<BlockModelGenerators>> parent() {
		return Optional.of(overlaySideBlockModelHolder);
	}

	@Override
	public Identifier finalize(BlockModelGenerators provider, FurtherExtendedModelTemplateBuilder model) {
		return model
			.buildExtended()
			.create(modLoc(name()), textures(), provider.modelOutput);
	}

	@Override
	protected void defineTextureSlots(ExtendedTextureMapping mapping) {
		mapping
			.putForced(TextureSlot.END, new Material(modLoc(name("top"))))
			.putForced(TextureSlot.SIDE, new Material(modLoc(name("side"))))
			.putForced(ModTextureSlots.OVERLAY_END, new Material(modLoc(name("top_overlay"))))
			.putForced(ModTextureSlots.OVERLAY_SIDE, new Material(modLoc(name("side_overlay"))));
	}

	@Override
	public boolean hasBlockState() {
		return true;
	}

	@Override
	public Optional<String> lang() {
		return Optional.of("Glistering Melon");
	}

	private Identifier modLoc(String path) {
		return Identifier.fromNamespaceAndPath(MelonMod.MODID, path);
	}
}

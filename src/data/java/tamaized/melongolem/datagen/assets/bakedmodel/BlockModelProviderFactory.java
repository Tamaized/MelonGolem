package tamaized.melongolem.datagen.assets.bakedmodel;

import net.minecraft.client.data.models.BlockModelGenerators;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.assets.bakedmodel.block.GlisteringMelonBlockModelHolder;
import tamaized.melongolem.datagen.assets.bakedmodel.block.overlay.OverlayBaseBlockModelHolder;
import tamaized.melongolem.datagen.assets.bakedmodel.block.overlay.OverlaySideBlockModelHolder;
import tamaized.melongolem.registry.ModBlocks;

@Component
public class BlockModelProviderFactory {

	@Autowired
	private ModBlocks blocks;

	@Autowired
	private OverlayBaseBlockModelHolder overlayBaseBlockModelHolder;

	@Autowired
	private OverlaySideBlockModelHolder overlaySideBlockModelHolder;

	@Autowired
	private GlisteringMelonBlockModelHolder glisteringMelonBlockModelHolder;

	public void make(BlockModelGenerators generators) {
		overlayBaseBlockModelHolder.build(generators);
		overlaySideBlockModelHolder.build(generators);
		glisteringMelonBlockModelHolder.build(generators);

		generators.blockStateOutput.accept(
			BlockModelGenerators.createSimpleBlock(
				blocks.GLISTERING_MELON.get(),
				BlockModelGenerators.plainVariant(glisteringMelonBlockModelHolder.get().orElseThrow())));
	}

}

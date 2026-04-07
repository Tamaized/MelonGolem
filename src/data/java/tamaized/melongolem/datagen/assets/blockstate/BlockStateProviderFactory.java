package tamaized.melongolem.datagen.assets.blockstate;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.datagen.assets.bakedmodel.block.GlisteringMelonBlockModelHolder;
import tamaized.melongolem.registry.ModBlocks;

@Component
public class BlockStateProviderFactory {

	@Autowired
	private ModBlocks blocks;

	@Autowired
	private GlisteringMelonBlockModelHolder glisteringMelonBlockModelHolder;

	public BlockStateProvider make(GatherDataEvent event) {
		return new BlockStateProvider(
			event.getGenerator().getPackOutput(),
			MelonMod.MODID,
			event.getExistingFileHelper()
		) {
			@Override
			protected void registerStatesAndModels() {
				simpleBlockWithItem(blocks.GLISTERING_MELON.get(), glisteringMelonBlockModelHolder.get().orElseThrow());
			}
		};
	}

}

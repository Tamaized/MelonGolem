package tamaized.melongolem.datagen.bakedmodel;

import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.datagen.bakedmodel.block.GlisteringMelonBlockModelHolder;
import tamaized.melongolem.datagen.bakedmodel.block.overlay.OverlayBaseBlockModelHolder;
import tamaized.melongolem.datagen.bakedmodel.block.overlay.OverlaySideBlockModelHolder;

@Component
public class BlockModelProviderFactory {

	@Autowired
	private OverlayBaseBlockModelHolder overlayBaseBlockModelHolder;

	@Autowired
	private OverlaySideBlockModelHolder overlaySideBlockModelHolder;

	@Autowired
	private GlisteringMelonBlockModelHolder glisteringMelonBlockModelHolder;

	public BlockModelProvider make(GatherDataEvent event) {
		return new BlockModelProvider(
			event.getGenerator().getPackOutput(),
			MelonMod.MODID,
			event.getExistingFileHelper()
		) {
			@Override
			protected void registerModels() {
				overlayBaseBlockModelHolder.build(this);
				overlaySideBlockModelHolder.build(this);
				glisteringMelonBlockModelHolder.build(this);
			}
		};
	}

}

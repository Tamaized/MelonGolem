package tamaized.melongolem.datagen.assets.bakedmodel;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.datagen.assets.bakedmodel.block.GlisteringMelonBlockModelHolder;
import tamaized.melongolem.datagen.assets.bakedmodel.block.overlay.OverlayBaseBlockModelHolder;
import tamaized.melongolem.datagen.assets.bakedmodel.block.overlay.OverlaySideBlockModelHolder;

@Component
public class BlockModelProviderFactory {

	@Autowired
	private OverlayBaseBlockModelHolder overlayBaseBlockModelHolder;

	@Autowired
	private OverlaySideBlockModelHolder overlaySideBlockModelHolder;

	@Autowired
	private GlisteringMelonBlockModelHolder glisteringMelonBlockModelHolder;

	public void make(BlockModelGenerators generators) {
		overlayBaseBlockModelHolder.build(this);
		overlaySideBlockModelHolder.build(this);
		glisteringMelonBlockModelHolder.build(this);
	}

}

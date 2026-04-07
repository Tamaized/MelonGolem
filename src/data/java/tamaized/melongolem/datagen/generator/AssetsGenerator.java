package tamaized.melongolem.datagen.generator;

import net.minecraft.DetectedVersion;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.assets.bakedmodel.BlockModelProviderFactory;
import tamaized.melongolem.datagen.assets.bakedmodel.ItemModelProviderFactory;
import tamaized.melongolem.datagen.assets.bakedmodel.ModelProviderFactory;
import tamaized.melongolem.datagen.assets.blockstate.BlockStateProviderFactory;
import tamaized.melongolem.datagen.assets.lang.LangProviderFactory;

import java.util.Optional;

@Component
public class AssetsGenerator {

	@Autowired
	private ModelProviderFactory modelProviderFactory;

	@Autowired
	private BlockModelProviderFactory blockModelProviderFactory;

	@Autowired
	private ItemModelProviderFactory itemModelProviderFactory;

	@Autowired
	private BlockStateProviderFactory blockStateProviderFactory;

	@Autowired
	private LangProviderFactory langProviderFactory;

	public void generate(GatherDataEvent.Client event) {
		event.getGenerator().addProvider(true, modelProviderFactory.make(event));
		event.getGenerator().addProvider(true, itemModelProviderFactory.make(event));
		event.getGenerator().addProvider(true, blockStateProviderFactory.make(event));
		event.getGenerator().addProvider(true, langProviderFactory.make(event));
	}

}

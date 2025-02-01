package tamaized.melongolem.datagen.generator;

import net.minecraft.DetectedVersion;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.bakedmodel.BlockModelProviderFactory;
import tamaized.melongolem.datagen.bakedmodel.ItemModelProviderFactory;
import tamaized.melongolem.datagen.blockstate.BlockStateProviderFactory;
import tamaized.melongolem.datagen.lang.LangProviderFactory;

import java.util.Optional;

@Component
public class ClientGenerator {

	@Autowired
	private BlockModelProviderFactory blockModelProviderFactory;

	@Autowired
	private ItemModelProviderFactory itemModelProviderFactory;

	@Autowired
	private BlockStateProviderFactory blockStateProviderFactory;

	@Autowired
	private LangProviderFactory langProviderFactory;

	public void generate(GatherDataEvent event) {
		event.getGenerator().addProvider(event.includeClient(), blockModelProviderFactory.make(event));
		event.getGenerator().addProvider(event.includeClient(), itemModelProviderFactory.make(event));
		event.getGenerator().addProvider(event.includeClient(), blockStateProviderFactory.make(event));
		event.getGenerator().addProvider(event.includeClient(), langProviderFactory.make(event));

		event.getGenerator().addProvider(true, new PackMetadataGenerator(event.getGenerator().getPackOutput())
			.add(PackMetadataSection.TYPE, new PackMetadataSection(
					net.minecraft.network.chat.Component.literal("Resources for MelonGolem"),
					DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
					Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE))
				)
			)
		);
	}

}

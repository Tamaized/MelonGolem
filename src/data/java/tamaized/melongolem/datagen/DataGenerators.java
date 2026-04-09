package tamaized.melongolem.datagen;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.beanification.PostConstruct;
import tamaized.melongolem.datagen.generator.AssetsGenerator;
import tamaized.melongolem.datagen.generator.DataGenerator;

@Component
public class DataGenerators {

	@Autowired
	private AssetsGenerator assetsGenerator;

	@Autowired
	private DataGenerator dataGenerator;

	@PostConstruct
	private void register(IEventBus bus) {
		bus.addListener(GatherDataEvent.Client.class, event -> assetsGenerator.generate(event));
		bus.addListener(GatherDataEvent.Server.class, event -> dataGenerator.generate(event));
	}

}

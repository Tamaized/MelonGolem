package tamaized.melongolem.datagen;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.beanification.PostConstruct;
import tamaized.melongolem.datagen.generator.ClientGenerator;

@Component
public class DataGenerators {

	@Autowired
	private ClientGenerator clientGenerator;

	@PostConstruct
	private void register(IEventBus bus) {
		bus.addListener(GatherDataEvent.class, event -> {
			clientGenerator.generate(event);
		});
	}

}

package tamaized.melongolem.datagen.generator;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.data.loot.LootTableProviderFactory;

@Component
public class DataGenerator {

	@Autowired
	private LootTableProviderFactory lootTableProviderFactory;

	public void generate(GatherDataEvent event) {
		event.getGenerator().addProvider(event.includeServer(), lootTableProviderFactory.make(event));
	}

}

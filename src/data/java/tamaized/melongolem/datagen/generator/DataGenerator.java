package tamaized.melongolem.datagen.generator;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.data.loot.LootTableProviderFactory;
import tamaized.melongolem.datagen.data.recipe.RecipeProviderFactory;

@Component
public class DataGenerator {

	@Autowired
	private LootTableProviderFactory lootTableProviderFactory;

	@Autowired
	private RecipeProviderFactory recipeProviderFactory;

	public void generate(GatherDataEvent event) {
		event.getGenerator().addProvider(event.includeServer(), lootTableProviderFactory.make(event));
		event.getGenerator().addProvider(event.includeServer(), recipeProviderFactory.make(event));
	}

}

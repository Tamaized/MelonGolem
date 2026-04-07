package tamaized.melongolem.datagen.generator;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.data.loot.LootTableProviderFactory;
import tamaized.melongolem.datagen.data.recipe.RecipeProviderFactory;
import tamaized.melongolem.datagen.data.tag.BlockTagProviderFactory;

@Component
public class DataGenerator {

	@Autowired
	private LootTableProviderFactory lootTableProviderFactory;

	@Autowired
	private RecipeProviderFactory recipeProviderFactory;

	@Autowired
	private BlockTagProviderFactory blockTagProviderFactory;

	public void generate(GatherDataEvent.Server event) {
		event.getGenerator().addProvider(true, lootTableProviderFactory.make(event));
		event.getGenerator().addProvider(true, recipeProviderFactory.make(event));
		event.getGenerator().addProvider(true, blockTagProviderFactory.make(event));
	}

}

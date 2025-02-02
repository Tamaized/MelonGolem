package tamaized.melongolem.datagen.data.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.RegistryProvider;

@Component
public class RecipeProviderFactory {

	@Autowired
	private RegistryProvider registryProvider;

	@Autowired
	private GlisteringMelonBlockRecipeFactory glisteringMelonBlockRecipeFactory;

	@Autowired
	private MelonStickRecipeFactory melonStickRecipeFactory;

	public RecipeProvider make(GatherDataEvent event) {
		return new RecipeProvider(
			event.getGenerator().getPackOutput(),
			registryProvider.retrieve(event)
		) {
			@Override
			protected void buildRecipes(RecipeOutput recipeOutput) {
				glisteringMelonBlockRecipeFactory.make(recipeOutput);
				melonStickRecipeFactory.make(recipeOutput);
			}

		};
	}

}

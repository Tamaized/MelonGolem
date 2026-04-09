package tamaized.melongolem.datagen.data.recipe;

import net.minecraft.core.HolderLookup;
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

	public RecipeProvider.Runner make(GatherDataEvent.Server event) {
		return new RecipeProvider.Runner(
			event.getGenerator().getPackOutput(),
			event.getLookupProvider()
		) {

			@Override
			public String getName() {
				return "Melon Golem Recipes";
			}

			@Override
			protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
				return new RecipeProvider(provider, output) {
					@Override
					protected void buildRecipes() {
						glisteringMelonBlockRecipeFactory.make(provider);
						melonStickRecipeFactory.make(provider);
					}

				};
			}
		};

	}
}

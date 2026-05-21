package tamaized.melongolem.datagen.data.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Component;
import tamaized.beanification.Directory;
import tamaized.datagenutil.data.recipe.RecipeHolder;

import java.util.List;

@Component
public class RecipeProviderFactory {

	@Directory(RecipeHolder.class)
	private List<RecipeHolder> recipeHolders;

	public RecipeProvider.Runner make(GatherDataEvent.Client event) {
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
						recipeHolders.forEach(holder -> holder.make(provider, output));
					}

				};
			}
		};

	}
}

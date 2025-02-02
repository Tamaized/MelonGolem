package tamaized.melongolem.datagen.data.recipe;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.util.RecipeProviderUtil;
import tamaized.melongolem.registry.ModBlocks;

@Component
public class GlisteringMelonBlockRecipeFactory {

	@Autowired
	private RecipeProviderUtil recipeProviderUtil;

	@Autowired
	private ModBlocks blocks;

	public void make(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, blocks.GLISTERING_MELON.get())
			.pattern("MMM")
			.pattern("MMM")
			.pattern("MMM")
			.define('M', Items.GLISTERING_MELON_SLICE)
			.unlockedBy("has_M", recipeProviderUtil.has(Items.GLISTERING_MELON_SLICE))
			.showNotification(true)
			.save(recipeOutput);
	}

}

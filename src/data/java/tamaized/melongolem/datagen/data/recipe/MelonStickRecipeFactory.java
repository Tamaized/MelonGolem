package tamaized.melongolem.datagen.data.recipe;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.util.RecipeProviderUtil;
import tamaized.melongolem.registry.ModBlocks;
import tamaized.melongolem.registry.ModItems;

@Component
public class MelonStickRecipeFactory {

	@Autowired
	private RecipeProviderUtil recipeProviderUtil;

	@Autowired
	private ModItems items;

	public void make(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, items.MELON_STICK.get())
			.pattern(" M")
			.pattern("S ")
			.define('M', Items.GLISTERING_MELON_SLICE)
			.define('S', Items.STICK)
			.unlockedBy("has_M", recipeProviderUtil.has(Items.GLISTERING_MELON_SLICE))
			.unlockedBy("has_S", recipeProviderUtil.has(Items.STICK))
			.showNotification(true)
			.save(recipeOutput);
	}

}

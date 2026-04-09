package tamaized.melongolem.datagen.data.recipe;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.util.RecipeProviderUtil;
import tamaized.melongolem.registry.ModItems;

@Component
public class MelonStickRecipeFactory {

	@Autowired
	private RecipeProviderUtil recipeProviderUtil;

	@Autowired
	private ModItems items;

	public void make(HolderLookup.Provider recipeOutput) {
		HolderGetter<Item> registry = recipeOutput.lookupOrThrow(Registries.ITEM);
		ShapedRecipeBuilder.shaped(registry, RecipeCategory.BUILDING_BLOCKS, items.MELON_STICK.get())
			.pattern(" M")
			.pattern("S ")
			.define('M', Items.GLISTERING_MELON_SLICE)
			.define('S', Items.STICK)
			.unlockedBy("has_M", recipeProviderUtil.has(registry, Items.GLISTERING_MELON_SLICE))
			.unlockedBy("has_S", recipeProviderUtil.has(registry, Items.STICK))
			.showNotification(true);
	}

}

package tamaized.melongolem.datagen.util;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import tamaized.beanification.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class RecipeProviderUtil {

	public Criterion<InventoryChangeTrigger.TriggerInstance> has(HolderGetter<Item> registry, ItemLike itemLike) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(registry, new ItemLike[]{itemLike}));
	}

	public Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... items) {
		return inventoryTrigger(Arrays.stream(items).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
	}

	public Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... predicates) {
		return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(predicates)));
	}

}

package tamaized.melongolem.datagen.data.loot.sub.entity;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.data.loot.sub.EntityLootTableSubProvider;
import tamaized.melongolem.registry.ModEntities;

@Component
public class MelonGolemEntityLootTableFactory {

	@Autowired
	private ModEntities entities;

	public void add(EntityLootTableSubProvider provider) {
		provider.add(entities.MELON_GOLEM.get(),
			LootTable.lootTable()
				.withPool(
					LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(
							LootItem.lootTableItem(Items.MELON_SLICE)
								.setWeight(1)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 15)))
						)
				)
		);
	}

}

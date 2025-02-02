package tamaized.melongolem.datagen.data.loot.sub.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.RegistryProvider;
import tamaized.melongolem.datagen.data.loot.sub.BlockLootTableSubProvider;
import tamaized.melongolem.registry.ModBlocks;

@Component
public class GlisteringMelonBlockLootTableFactory {

	@Autowired
	private RegistryProvider registries;

	@Autowired
	private ModBlocks blocks;

	public void add(BlockLootTableSubProvider provider) {
		provider.add(blocks.GLISTERING_MELON.get(),
			LootTable.lootTable()
				.withPool(
					LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.setBonusRolls(ConstantValue.exactly(0))
						.add(
							AlternativesEntry.alternatives(
								LootItem.lootTableItem(blocks.ITEMBLOCK_GLISTERING_MELON.get())
									.when(provider.hasSilkTouch()),
								LootItem.lootTableItem(Items.GLISTERING_MELON_SLICE)
									.apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 7), false))
									.apply(ApplyBonusCount.addUniformBonusCount(registries.join().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE), 1))
									.apply(LimitCount.limitCount(IntRange.upperBound(9)))
									.apply(ApplyExplosionDecay.explosionDecay())
							)
						)
				)
		);
	}

}

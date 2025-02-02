package tamaized.melongolem.datagen.data.loot.sub.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.RegistryProvider;
import tamaized.melongolem.registry.ModBlocks;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Component
public class GlisteringMelonBlockLootTableFactory {

	@Autowired
	private RegistryProvider registries;

	@Autowired
	private ModBlocks blocks;

	public void add(BiConsumer<Block, LootTable.Builder> add, Supplier<LootItemCondition.Builder> hasSilkTouch) {
		add.accept(blocks.GLISTERING_MELON.get(),
			LootTable.lootTable()
				.withPool(
					LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.setBonusRolls(ConstantValue.exactly(0))
						.add(
							AlternativesEntry.alternatives(
								LootItem.lootTableItem(blocks.ITEMBLOCK_GLISTERING_MELON.get())
									.when(hasSilkTouch.get()),
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

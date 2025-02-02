package tamaized.melongolem.datagen.data.loot.sub;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import tamaized.beanification.Autowired;
import tamaized.beanification.Configurable;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.datagen.data.loot.sub.block.GlisteringMelonBlockLootTableFactory;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Configurable
public class BlockLootTableSubProvider extends BlockLootSubProvider {

	@Autowired
	private GlisteringMelonBlockLootTableFactory glisteringMelonBlockLootTableFactory;

	public BlockLootTableSubProvider(HolderLookup.Provider registries) {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
	}

	@Override
	public void generate() {
		getKnownBlocksStream().forEach(e -> add(e, LootTable.lootTable()));
		glisteringMelonBlockLootTableFactory.add(this::add, this::hasSilkTouch);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return getKnownBlocksStream().toList();
	}

	private Stream<Block> getKnownBlocksStream() {
		return registries.lookupOrThrow(Registries.BLOCK)
			.listElements()
			.filter(r -> Objects.requireNonNull(r.getKey()).location().getNamespace().equals(MelonMod.MODID))
			.map(Holder.Reference::value);
	}
}

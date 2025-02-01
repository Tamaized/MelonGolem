package tamaized.melongolem.datagen.data.loot.sub;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;
import tamaized.beanification.Autowired;
import tamaized.beanification.Configurable;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.datagen.data.loot.sub.entity.GlisteringMelonGolemEntityLootTableFactory;
import tamaized.melongolem.datagen.data.loot.sub.entity.MelonGolemEntityLootTableFactory;

import java.util.Objects;
import java.util.stream.Stream;

@Configurable
public class EntityLootTableSubProvider extends EntityLootSubProvider {

	@Autowired
	private MelonGolemEntityLootTableFactory melonGolemEntityLootTableFactory;

	@Autowired
	private GlisteringMelonGolemEntityLootTableFactory glisteringMelonGolemEntityLootTableFactory;

	public EntityLootTableSubProvider(HolderLookup.Provider registries) {
		super(FeatureFlags.REGISTRY.allFlags(), registries);
	}

	@Override
	public void generate() {
		getKnownEntityTypes().filter(this::canHaveLootTable).forEach(e -> add(e, LootTable.lootTable()));
		melonGolemEntityLootTableFactory.add(this);
		glisteringMelonGolemEntityLootTableFactory.add(this);
	}

	// Increased visibility
	@Override
	public void add(EntityType<?> entityType, LootTable.Builder builder) {
		super.add(entityType, builder);
	}

	@Override
	protected Stream<EntityType<?>> getKnownEntityTypes() {
		return registries.lookupOrThrow(Registries.ENTITY_TYPE)
			.listElements()
			.filter(r -> Objects.requireNonNull(r.getKey()).location().getNamespace().equals(MelonMod.MODID))
			.map(Holder.Reference::value);
	}
}

package tamaized.melongolem.common;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import tamaized.melongolem.MelonMod;

import javax.annotation.Nullable;
import java.util.Objects;

public class EntityGlisteringMelonGolem extends EntityMelonGolem {

	private static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation(MelonMod.MODID, "glisteringmelongolem"));

	public EntityGlisteringMelonGolem(World worldIn) {
		super(Objects.requireNonNull(MelonMod.entityTypeGlisteringMelonGolem), worldIn);
	}

	@Override
	@Nullable
	protected ResourceLocation getLootTable() {
		return LOOT;
	}
}

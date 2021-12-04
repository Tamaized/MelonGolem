package tamaized.melongolem.common;

import net.minecraft.world.level.Level;
import tamaized.melongolem.MelonMod;

import java.util.Objects;

public class EntityGlisteringMelonGolem extends EntityMelonGolem {

	//	private static final ResourceLocation LOOT = LootTables.register(new ResourceLocation(MelonMod.MODID, "glisteringmelongolem"));

	public EntityGlisteringMelonGolem(Level worldIn) {
		super(Objects.requireNonNull(MelonMod.entityTypeGlisteringMelonGolem), worldIn);
	}

	/*@Override
	@Nullable
	protected ResourceLocation getLootTable() {
		return LOOT;
	}*/
}

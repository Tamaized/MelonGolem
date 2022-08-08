package tamaized.melongolem.common;

import net.minecraft.world.level.Level;
import tamaized.melongolem.MelonMod;

import java.util.Objects;

public class EntityGlisteringMelonGolem extends EntityMelonGolem {

	public EntityGlisteringMelonGolem(Level worldIn) {
		super(Objects.requireNonNull(MelonMod.ENTITY_TYPE_GLISTERING_MELON_GOLEM.get()), worldIn);
	}
}

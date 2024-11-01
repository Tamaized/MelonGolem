package tamaized.melongolem.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import tamaized.beanification.Autowired;
import tamaized.melongolem.registry.ModEntities;

public class EntityGlisteringMelonGolem extends EntityMelonGolem {

	@Autowired
	private static ModEntities modEntities;

	public EntityGlisteringMelonGolem(Level level) {
		super(modEntities.GLISTERING_MELON_GOLEM.get(), level);
	}

	public EntityGlisteringMelonGolem(EntityType<? extends EntityGlisteringMelonGolem> type, Level level){
		super(type, level);
	}
}

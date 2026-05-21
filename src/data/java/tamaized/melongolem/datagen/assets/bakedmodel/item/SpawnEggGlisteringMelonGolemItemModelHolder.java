package tamaized.melongolem.datagen.assets.bakedmodel.item;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.datagenutil.assets.bakedmodel.item.BasicItemModelHolder;
import tamaized.melongolem.registry.ModEntities;

@Component
public class SpawnEggGlisteringMelonGolemItemModelHolder extends BasicItemModelHolder {

	@Autowired
	private ModEntities entities;

	@Override
	protected DeferredHolder<Item, ? extends Item> itemForName() {
		return entities.SPAWN_EGG_GLISTERING_MELON_GOLEM;
	}

	@Override
	protected ModelTemplate template() {
		return ModelTemplates.FLAT_ITEM;
	}

}

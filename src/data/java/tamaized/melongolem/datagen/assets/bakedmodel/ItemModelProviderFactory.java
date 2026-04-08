package tamaized.melongolem.datagen.assets.bakedmodel;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.registry.ModEntities;
import tamaized.melongolem.registry.ModItems;

@Component
public class ItemModelProviderFactory {

	@Autowired
	private ModItems items;

	@Autowired
	private ModEntities entities;

	public void make(ItemModelGenerators event) {
		event.generateFlatItem(items.MELON_STICK.get(), ModelTemplates.FLAT_ITEM);
		event.generateFlatItem(entities.SPAWN_EGG_MELON_GOLEM.get(), ModelTemplates.FLAT_ITEM);
		event.generateFlatItem(entities.SPAWN_EGG_GLISTERING_MELON_GOLEM.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
	}
}

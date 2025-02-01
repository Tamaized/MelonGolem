package tamaized.melongolem.datagen.assets.bakedmodel;

import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.registry.ModEntities;
import tamaized.melongolem.registry.ModItems;

@Component
public class ItemModelProviderFactory {

	@Autowired
	private ModItems items;

	@Autowired
	private ModEntities entities;

	public ItemModelProvider make(GatherDataEvent event) {
		return new ItemModelProvider(
			event.getGenerator().getPackOutput(),
			MelonMod.MODID,
			event.getExistingFileHelper()
		) {
			@Override
			protected void registerModels() {
				handheldItem(items.MELON_STICK.get());
				spawnEggItem(entities.SPAWN_EGG_MELON_GOLEM.get());
				spawnEggItem(entities.SPAWN_EGG_GLISTERING_MELON_GOLEM.get());
			}
		};
	}

}

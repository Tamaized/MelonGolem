package tamaized.melongolem.datagen.assets.bakedmodel.item;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.datagenutil.assets.bakedmodel.item.BasicItemModelHolder;
import tamaized.melongolem.registry.ModItems;

import java.util.Optional;

@Component
public class MelonStickItemModelHolder extends BasicItemModelHolder {

	@Autowired
	private ModItems items;

	@Override
	protected DeferredHolder<Item, ? extends Item> itemForName() {
		return items.MELON_STICK;
	}

	@Override
	protected ModelTemplate template() {
		return ModelTemplates.FLAT_ITEM;
	}

	@Override
	public Optional<String> lang() {
		return Optional.of("Melon on a Stick");
	}
}

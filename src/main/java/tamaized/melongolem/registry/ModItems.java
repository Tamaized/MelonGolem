package tamaized.melongolem.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import tamaized.beanification.Component;
import tamaized.melongolem.common.ItemMelonStick;
import tamaized.regutil.RegUtil;

@Component
public class ModItems {

	public final DeferredHolder<Item, ItemMelonStick> MELON_STICK = RegUtil.register(Registries.ITEM, "melon_stick",
		(id) -> new ItemMelonStick(new Item.Properties()
			.setId(ResourceKey.create(Registries.ITEM, id))
		)
	);

}

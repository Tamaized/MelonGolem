package tamaized.melongolem.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import tamaized.beanification.Component;
import tamaized.melongolem.common.ItemMelonStick;
import tamaized.regutil.RegUtil;
import tamaized.regutil.RegistryClass;

import java.util.function.Supplier;

@Component
public class ModItems {

	final DeferredRegister<Item> REGISTRY = RegUtil.create(Registries.ITEM);

	public final Supplier<Item> MELON_STICK = REGISTRY.register("melon_stick", (id) -> new ItemMelonStick(new Item.Properties()
		.setId(ResourceKey.create(Registries.ITEM, id))
	));

}

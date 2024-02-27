package tamaized.melongolem.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import tamaized.melongolem.common.ItemMelonStick;
import tamaized.regutil.RegUtil;
import tamaized.regutil.RegistryClass;

import java.util.function.Supplier;

public class ModItems implements RegistryClass {

	static final DeferredRegister<Item> REGISTRY = RegUtil.create(Registries.ITEM);

	public static final Supplier<Item> MELON_STICK = REGISTRY.register("melon_stick", () -> new ItemMelonStick(new Item.Properties()));

	@Override
	public void init(IEventBus bus) {

	}

}

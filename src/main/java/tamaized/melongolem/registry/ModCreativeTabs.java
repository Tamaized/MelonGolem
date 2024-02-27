package tamaized.melongolem.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import tamaized.melongolem.MelonMod;
import tamaized.regutil.RegUtil;
import tamaized.regutil.RegistryClass;

import java.util.function.Supplier;

public class ModCreativeTabs implements RegistryClass {

	private static final DeferredRegister<CreativeModeTab> REGISTRY = RegUtil.create(Registries.CREATIVE_MODE_TAB);

	public static final Supplier<CreativeModeTab> TAB = REGISTRY.register("tab", () -> CreativeModeTab.builder()
			.title(Component.translatable(MelonMod.MODID + ".item_group"))
			.icon(() -> new ItemStack(Items.MELON_SLICE))
			.displayItems((parameters, output) -> {
				//// Blocks
				output.accept(ModBlocks.GLISTERING_MELON.get());
				//// Items
				output.accept(ModItems.MELON_STICK.get());
				//// Entities
				output.accept(ModEntities.SPAWN_EGG_MELON_GOLEM.get());
				output.accept(ModEntities.SPAWN_EGG_GLISTERING_MELON_GOLEM.get());
			})
			.build());

	@Override
	public void init(IEventBus bus) {

	}
}

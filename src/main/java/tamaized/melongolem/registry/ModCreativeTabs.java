package tamaized.melongolem.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import tamaized.beanification.Autowired;
import tamaized.melongolem.MelonMod;
import tamaized.regutil.RegUtil;

import java.util.function.Supplier;

@tamaized.beanification.Component
public class ModCreativeTabs {

	@Autowired
	private ModBlocks modBlocks;

	@Autowired
	private ModEntities modEntities;

	@Autowired
	private ModItems modItems;

	public final Supplier<CreativeModeTab> TAB = RegUtil.register(Registries.CREATIVE_MODE_TAB, "tab",
		() -> CreativeModeTab.builder()
			.title(Component.translatable(MelonMod.MODID + ".item_group"))
			.icon(() -> new ItemStack(Items.MELON_SLICE))
			.displayItems((_, output) -> {
				// Blocks
				output.accept(modBlocks.GLISTERING_MELON.get());
				// Items
				output.accept(modItems.MELON_STICK.get());
				// Entities
				output.accept(modEntities.SPAWN_EGG_MELON_GOLEM.get());
				output.accept(modEntities.SPAWN_EGG_GLISTERING_MELON_GOLEM.get());
			})
			.build()
	);

}

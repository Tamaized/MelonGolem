package tamaized.melongolem.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tamaized.beanification.Component;
import tamaized.regutil.RegUtil;

import java.util.function.Supplier;

@Component
public class ModBlocks {

	private final DeferredRegister<Block> REGISTRY = RegUtil.create(Registries.BLOCK);
	private final DeferredRegister<Item> ITEM_REGISTRY = RegUtil.create(Registries.ITEM);

	public final DeferredHolder<Block, Block> GLISTERING_MELON = REGISTRY.register("glistering_melon", () -> new Block(BlockBehaviour.Properties.of()
		.mapColor(MapColor.COLOR_LIGHT_GREEN)
		.pushReaction(PushReaction.DESTROY)
		.strength(1.0F)
		.sound(SoundType.WOOD)
		.lightLevel(state -> 4))
	);
	public Supplier<BlockItem> ITEMBLOCK_GLISTERING_MELON = ITEM_REGISTRY.register(
		GLISTERING_MELON.getId().getPath(),
		() -> new BlockItem(GLISTERING_MELON.get(), new Item.Properties().setNoRepair())
	);

}

package tamaized.melongolem.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import tamaized.beanification.Component;
import tamaized.regutil.RegUtil;

import java.util.function.Supplier;

@Component
public class ModBlocks {

	public final DeferredHolder<Block, Block> GLISTERING_MELON = RegUtil.register(Registries.BLOCK, "glistering_melon",
		(id) -> new Block(BlockBehaviour.Properties.of()
			.setId(ResourceKey.create(Registries.BLOCK, id))
			.mapColor(MapColor.COLOR_LIGHT_GREEN)
			.pushReaction(PushReaction.DESTROY)
			.strength(1.0F)
			.sound(SoundType.WOOD)
			.lightLevel(_ -> 4))
	);

	public Supplier<BlockItem> ITEMBLOCK_GLISTERING_MELON = RegUtil.register(Registries.ITEM, GLISTERING_MELON.getId().getPath(),
		(id) -> new BlockItem(GLISTERING_MELON.get(), new Item.Properties()
			.setId(ResourceKey.create(Registries.ITEM, id))
			.useBlockDescriptionPrefix()
		)
	);

}

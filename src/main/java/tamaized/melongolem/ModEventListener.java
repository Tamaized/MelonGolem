package tamaized.melongolem;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import tamaized.melongolem.common.EntityGlisteringMelonGolem;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.registry.ModBlocks;

import java.util.Set;

public class ModEventListener {

	private static Set<Block> MELONS;

	public static void init(IEventBus bus) {
		bus.addListener(PlayerInteractEvent.RightClickBlock.class, event -> {
			Player player = event.getEntity();
			Level world = event.getLevel();
			BlockPos vertex = event.getPos();
			if (MELONS == null)
				MELONS = ImmutableSet.of(

						Blocks.MELON,

						ModBlocks.GLISTERING_MELON.get()

				);
			for (Block melonCheck : MELONS) {
				if (!world.isClientSide && world.getBlockState(vertex).getBlock() == melonCheck && MelonConfig.compareStabbyItem(player.getItemInHand(InteractionHand.MAIN_HAND)) && MelonConfig.compareStabbyItem(player.getItemInHand(InteractionHand.OFF_HAND))) {
					if (world.getBlockState(vertex.below()).getBlock() == melonCheck && world.getBlockState(vertex.above()).getBlock() == melonCheck) {
						if (!player.isCreative()) {
							player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
							player.getItemInHand(InteractionHand.OFF_HAND).shrink(1);
						}
						world.removeBlock(vertex.below(), false);
						world.removeBlock(vertex, false);
						world.removeBlock(vertex.above(), false);
						EntityMelonGolem melon = melonCheck == ModBlocks.GLISTERING_MELON.get() ? new EntityGlisteringMelonGolem(world) : new EntityMelonGolem(world);
						melon.teleportTo(vertex.getX() + 0.5F, vertex.getY() - 0.5F, vertex.getZ() + 0.5F);
						world.addFreshEntity(melon);
						break;
					}
				}
			}
		});
	}

}

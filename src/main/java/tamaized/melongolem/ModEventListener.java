package tamaized.melongolem;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tamaized.melongolem.common.EntityGlisteringMelonGolem;
import tamaized.melongolem.common.EntityMelonGolem;

import java.util.Set;

@Mod.EventBusSubscriber(modid = MelonMod.MODID)
public class ModEventListener {

	private static Set<Block> MELONS;

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickBlock e) {
		Player player = e.getPlayer();
		Level world = e.getWorld();
		BlockPos vertex = e.getPos();
		if (MELONS == null)
			MELONS = ImmutableSet.of(

					Blocks.MELON,

					MelonMod.glisteringMelonBlock

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
					EntityMelonGolem melon = melonCheck == MelonMod.glisteringMelonBlock ? new EntityGlisteringMelonGolem(world) : new EntityMelonGolem(world);
					melon.teleportTo(vertex.getX() + 0.5F, vertex.getY() - 0.5F, vertex.getZ() + 0.5F);
					world.addFreshEntity(melon);
					break;
				}
			}
		}
	}

}

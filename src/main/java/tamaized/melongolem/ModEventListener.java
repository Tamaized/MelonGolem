package tamaized.melongolem;


import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
		PlayerEntity player = e.getPlayer();
		World world = e.getWorld();
		BlockPos pos = e.getPos();
		if (MELONS == null)
			MELONS = ImmutableSet.of(

					Blocks.MELON,

					MelonMod.glisteringMelonBlock

			);
		for (Block melonCheck : MELONS) {
			if (!world.isRemote && world.getBlockState(pos).getBlock() == melonCheck && MelonConfig.compareStabbyItem(player.getHeldItem(Hand.MAIN_HAND)) && MelonConfig.compareStabbyItem(player.getHeldItem(Hand.OFF_HAND))) {
				if (world.getBlockState(pos.down()).getBlock() == melonCheck && world.getBlockState(pos.up()).getBlock() == melonCheck) {
					if (!player.isCreative()) {
						player.getHeldItem(Hand.MAIN_HAND).shrink(1);
						player.getHeldItem(Hand.OFF_HAND).shrink(1);
					}
					world.removeBlock(pos.down(), false);
					world.removeBlock(pos, false);
					world.removeBlock(pos.up(), false);
					EntityMelonGolem melon = melonCheck == MelonMod.glisteringMelonBlock ? new EntityGlisteringMelonGolem(world) : new EntityMelonGolem(world);
					melon.setPositionAndUpdate(pos.getX() + 0.5F, pos.getY() - 0.5F, pos.getZ() + 0.5F);
					world.addEntity(melon);
					break;
				}
			}
		}
	}

}

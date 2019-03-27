package tamaized.melongolem;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tamaized.melongolem.common.EntityMelonGolem;

@Mod.EventBusSubscriber(modid = MelonMod.MODID)
public class ModEventListener {

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickBlock e) {
		EntityPlayer player = e.getEntityPlayer();
		World world = e.getWorld();
		BlockPos pos = e.getPos();
		if (!world.isRemote && world.getBlockState(pos).getBlock() == Blocks.MELON && MelonConfig.compareStabbyItem(player.getHeldItem(EnumHand.MAIN_HAND)) && MelonConfig.compareStabbyItem(player.getHeldItem(EnumHand.OFF_HAND))) {
			if (world.getBlockState(pos.down()).getBlock() == Blocks.MELON && world.getBlockState(pos.up()).getBlock() == Blocks.MELON) {
				if (!player.isCreative()) {
					player.getHeldItem(EnumHand.MAIN_HAND).shrink(1);
					player.getHeldItem(EnumHand.OFF_HAND).shrink(1);
				}
				world.removeBlock(pos.down());
				world.removeBlock(pos);
				world.removeBlock(pos.up());
				EntityMelonGolem melon = new EntityMelonGolem(world);
				melon.setPositionAndUpdate(pos.getX() + 0.5F, pos.getY() - 0.5F, pos.getZ() + 0.5F);
				world.spawnEntity(melon);
			}
		}
	}

}

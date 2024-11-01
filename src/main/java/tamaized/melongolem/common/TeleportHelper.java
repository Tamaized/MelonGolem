package tamaized.melongolem.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import tamaized.beanification.Component;

import java.util.Optional;

@Component
public class TeleportHelper {

	public Optional<BlockPos> findLocationAboveFriendlyBlock(Level world, Entity entity, BlockPos blockPos) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int l = world.getRandom().nextInt(6); l <= 8; ++l) {
			for (int i1 = world.getRandom().nextInt(6); i1 <= 8; ++i1) {
				for (int j = 3; j > -3; j--) {
					pos.set(blockPos.getX() + l, (blockPos.getY() + j) - 1, blockPos.getZ() + i1);
					BlockState iblockstate = world.getBlockState(pos);
					if (Block.canSupportCenter(world, pos, Direction.UP) && iblockstate.isValidSpawn(world, pos, entity.getType()) && world.isEmptyBlock(pos.above()) && world.isEmptyBlock(pos.above(2)))
						return Optional.of(pos.above());
				}
			}
		}
		return Optional.empty();
	}

}

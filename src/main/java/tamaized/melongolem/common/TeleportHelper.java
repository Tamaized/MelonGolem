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
		for (int attempts = 0; attempts < 32; attempts++) {
			BlockPos randomPos = world.getBlockRandomPos(blockPos.getX() - 8, blockPos.getY(), blockPos.getZ() - 8, 0);
			for (int yOffset = 5; yOffset > -5; yOffset--) {
				BlockPos pos = randomPos.above(yOffset);
				BlockState iblockstate = world.getBlockState(pos);
				if (Block.canSupportCenter(world, pos, Direction.UP) && iblockstate.isValidSpawn(world, pos, entity.getType()) && world.isEmptyBlock(pos.above()) && world.isEmptyBlock(pos.above(2)))
					return Optional.of(pos.above());
			}
		}
		return Optional.empty();
	}

}

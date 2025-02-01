package tamaized.melongolem.datagen.bakedmodel.block.overlay;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.bakedmodel.block.BlockModelHolder;

@Component
public class OverlayBaseBlockModelHolder extends BlockModelHolder {

	public void build(BlockModelProvider provider) {
		set(
			provider.withExistingParent("block/overlay/base", "block/block")
				.element()
				.from(0, 0, 0).to(16, 16, 16)
				.face(Direction.DOWN).texture("#down").cullface(Direction.DOWN).end()
				.face(Direction.UP).texture("#up").cullface(Direction.UP).end()
				.face(Direction.NORTH).texture("#north").cullface(Direction.NORTH).end()
				.face(Direction.SOUTH).texture("#south").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).texture("#west").cullface(Direction.WEST).end()
				.face(Direction.EAST).texture("#east").cullface(Direction.EAST).end()
				.end()
				.element()
				.from(0, 0, 0).to(16, 16, 16)
				.face(Direction.DOWN).texture("#overlay-down").cullface(Direction.DOWN).emissivity(15, 15).end()
				.face(Direction.UP).texture("#overlay-up").cullface(Direction.UP).emissivity(15, 15).end()
				.face(Direction.NORTH).texture("#overlay-north").cullface(Direction.NORTH).emissivity(15, 15).end()
				.face(Direction.SOUTH).texture("#overlay-south").cullface(Direction.SOUTH).emissivity(15, 15).end()
				.face(Direction.WEST).texture("#overlay-west").cullface(Direction.WEST).emissivity(15, 15).end()
				.face(Direction.EAST).texture("#overlay-east").cullface(Direction.EAST).emissivity(15, 15).end()
				.end()
		);
	}

}

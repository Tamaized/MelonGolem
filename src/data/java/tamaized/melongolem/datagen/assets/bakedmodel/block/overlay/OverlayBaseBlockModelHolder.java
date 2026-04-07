package tamaized.melongolem.datagen.assets.bakedmodel.block.overlay;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.assets.bakedmodel.block.BlockModelHolder;

@Component
public class OverlayBaseBlockModelHolder extends BlockModelHolder {

	public void build(BlockModelGenerators provider) {
		set(
			new ExtendedModelTemplateBuilder()
				.parent(Identifier.withDefaultNamespace("block/block"))
				.element(elementBuilder -> elementBuilder
					.from(0, 0, 0).to(16, 16, 16)
					.face(Direction.DOWN, fb -> fb.texture(TextureSlot.DOWN).cullface(Direction.DOWN))
					.face(Direction.UP, fb -> fb.texture(TextureSlot.UP).cullface(Direction.UP))
					.face(Direction.NORTH, fb -> fb.texture(TextureSlot.NORTH).cullface(Direction.NORTH))
					.face(Direction.SOUTH, fb -> fb.texture(TextureSlot.SOUTH).cullface(Direction.SOUTH))
					.face(Direction.WEST, fb -> fb.texture(TextureSlot.WEST).cullface(Direction.WEST))
					.face(Direction.EAST, fb -> fb.texture(TextureSlot.EAST).cullface(Direction.EAST)))
				.element( elementBuilder -> elementBuilder
					.from(0, 0, 0).to(16, 16, 16)
					.face(Direction.DOWN, fb -> fb.texture("#overlay-down").cullface(Direction.DOWN).emissivity(15, 15).end()
					.face(Direction.UP).texture("#overlay-up").cullface(Direction.UP).emissivity(15, 15).end()
					.face(Direction.NORTH).texture("#overlay-north").cullface(Direction.NORTH).emissivity(15, 15).end()
					.face(Direction.SOUTH).texture("#overlay-south").cullface(Direction.SOUTH).emissivity(15, 15).end()
					.face(Direction.WEST).texture("#overlay-west").cullface(Direction.WEST).emissivity(15, 15).end()
					.face(Direction.EAST).texture("#overlay-east").cullface(Direction.EAST).emissivity(15, 15).end()
					.end())

		);
	}

}

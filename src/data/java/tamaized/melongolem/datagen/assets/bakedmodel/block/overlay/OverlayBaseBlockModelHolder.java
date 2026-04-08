package tamaized.melongolem.datagen.assets.bakedmodel.block.overlay;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.datagen.assets.bakedmodel.block.BlockModelHolder;
import tamaized.melongolem.datagen.util.ModTextureSlots;

@Component
public class OverlayBaseBlockModelHolder extends BlockModelHolder {

	public void build(BlockModelGenerators provider) {
		TextureMapping mapping = new TextureMapping();

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
				.element(elementBuilder -> elementBuilder
					.from(0, 0, 0).to(16, 16, 16)
					.face(Direction.DOWN, fb -> fb.texture(ModTextureSlots.OVERLAY_DOWN).cullface(Direction.DOWN).lightEmission(5))
					.face(Direction.UP, fb -> fb.texture(ModTextureSlots.OVERLAY_UP).cullface(Direction.UP).lightEmission(15))
					.face(Direction.NORTH, fb -> fb.texture(ModTextureSlots.OVERLAY_NORTH).cullface(Direction.NORTH).lightEmission(15))
					.face(Direction.SOUTH, fb -> fb.texture(ModTextureSlots.OVERLAY_SOUTH).cullface(Direction.SOUTH).lightEmission(15))
					.face(Direction.WEST, fb -> fb.texture(ModTextureSlots.OVERLAY_WEST).cullface(Direction.WEST).lightEmission(15))
					.face(Direction.EAST, fb -> fb.texture(ModTextureSlots.OVERLAY_EAST).cullface(Direction.EAST).lightEmission(15)))
				.build()
				.create(Identifier.fromNamespaceAndPath(MelonMod.MODID, "block/overlay/base"), mapping, provider.modelOutput)
		);
	}

}

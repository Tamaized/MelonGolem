package tamaized.melongolem.datagen.assets.bakedmodel.block.overlay;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.datagenutil.assets.bakedmodel.BlockModelHolder;
import tamaized.datagenutil.assets.bakedmodel.ExtendedTextureMapping;
import tamaized.datagenutil.assets.bakedmodel.FurtherExtendedModelTemplateBuilder;
import tamaized.datagenutil.assets.bakedmodel.ModelHolder;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.datagen.util.ModTextureSlots;

import java.util.Optional;

@Component
public class OverlaySideBlockModelHolder extends BlockModelHolder {

	@Autowired
	private OverlayBaseBlockModelHolder overlayBaseBlockModelHolder;

	@Override
	public Optional<ModelHolder<BlockModelGenerators>> parent() {
		return Optional.of(overlayBaseBlockModelHolder);
	}

	@Override
	public Identifier finalize(BlockModelGenerators provider, FurtherExtendedModelTemplateBuilder model) {
		return model
			.buildExtended()
			.create(Identifier.fromNamespaceAndPath(MelonMod.MODID, "block/overlay/side"), textures(), provider.modelOutput);
	}

	@Override
	protected void defineTextureSlots(ExtendedTextureMapping mapping) {
		mapping
			.putRef(TextureSlot.DOWN, TextureSlot.END)
			.putRef(TextureSlot.UP, TextureSlot.END)
			.putRef(TextureSlot.NORTH, TextureSlot.SIDE)
			.putRef(TextureSlot.EAST, TextureSlot.SIDE)
			.putRef(TextureSlot.SOUTH, TextureSlot.SIDE)
			.putRef(TextureSlot.WEST, TextureSlot.SIDE)
			.putRef(ModTextureSlots.OVERLAY_DOWN, ModTextureSlots.OVERLAY_END)
			.putRef(ModTextureSlots.OVERLAY_UP, ModTextureSlots.OVERLAY_END)
			.putRef(ModTextureSlots.OVERLAY_NORTH, ModTextureSlots.OVERLAY_SIDE)
			.putRef(ModTextureSlots.OVERLAY_EAST, ModTextureSlots.OVERLAY_SIDE)
			.putRef(ModTextureSlots.OVERLAY_SOUTH, ModTextureSlots.OVERLAY_SIDE)
			.putRef(ModTextureSlots.OVERLAY_WEST, ModTextureSlots.OVERLAY_SIDE);
	}
}

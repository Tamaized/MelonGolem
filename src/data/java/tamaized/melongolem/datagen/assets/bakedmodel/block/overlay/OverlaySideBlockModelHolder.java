package tamaized.melongolem.datagen.assets.bakedmodel.block.overlay;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.datagen.assets.bakedmodel.block.BlockModelHolder;
import tamaized.melongolem.datagen.util.ModTextureSlots;

@Component
public class OverlaySideBlockModelHolder extends BlockModelHolder {

	@Autowired
	private OverlayBaseBlockModelHolder overlayBaseBlockModelHolder;

	public void build(BlockModelGenerators provider) {
		TextureMapping mapping = new TextureMapping()
			.copySlot(TextureSlot.PARTICLE, TextureSlot.SIDE)
			.copySlot(TextureSlot.DOWN, TextureSlot.END)
			.copySlot(TextureSlot.UP, TextureSlot.END)
			.copySlot(TextureSlot.NORTH, TextureSlot.SIDE)
			.copySlot(TextureSlot.EAST, TextureSlot.SIDE)
			.copySlot(TextureSlot.SOUTH, TextureSlot.SIDE)
			.copySlot(TextureSlot.WEST, TextureSlot.SIDE)
			.copySlot(ModTextureSlots.OVERLAY_DOWN, ModTextureSlots.OVERLAY_END)
			.copySlot(ModTextureSlots.OVERLAY_UP, ModTextureSlots.OVERLAY_END)
			.copySlot(ModTextureSlots.OVERLAY_NORTH, ModTextureSlots.OVERLAY_SIDE)
			.copySlot(ModTextureSlots.OVERLAY_EAST, ModTextureSlots.OVERLAY_SIDE)
			.copySlot(ModTextureSlots.OVERLAY_SOUTH, ModTextureSlots.OVERLAY_SIDE)
			.copySlot(ModTextureSlots.OVERLAY_WEST, ModTextureSlots.OVERLAY_WEST);

		overlayBaseBlockModelHolder.get().ifPresent(parent -> set(
			new ExtendedModelTemplateBuilder()
				.parent(parent)
				.build()
				.create(Identifier.fromNamespaceAndPath(MelonMod.MODID, "block/overlay/side"), mapping, provider.modelOutput)
		));
	}

}

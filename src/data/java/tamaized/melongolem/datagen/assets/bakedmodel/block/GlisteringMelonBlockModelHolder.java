package tamaized.melongolem.datagen.assets.bakedmodel.block;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.datagen.assets.bakedmodel.block.overlay.OverlaySideBlockModelHolder;
import tamaized.melongolem.datagen.util.ModTextureSlots;

@Component
public class GlisteringMelonBlockModelHolder extends BlockModelHolder {

	@Autowired
	private OverlaySideBlockModelHolder overlaySideBlockModelHolder;

	public void build(BlockModelGenerators provider) {
		TextureMapping mapping = new TextureMapping()
			.put(TextureSlot.END, new Material(modLoc("block/glistening_melon_top")))
			.put(TextureSlot.SIDE, new Material(modLoc("block/glistening_melon_side")))
			.put(ModTextureSlots.OVERLAY_END, new Material(modLoc("block/glistening_melon_top_overlay")))
			.put(ModTextureSlots.OVERLAY_SIDE, new Material(modLoc("block/glistening_melon_side_overlay")));

		overlaySideBlockModelHolder.get().ifPresent(parent -> set(
			new ExtendedModelTemplateBuilder()
				.parent(parent)
				.build()
				.create(modLoc("block/glistering_melon"), mapping, provider.modelOutput)
		));
	}

	private Identifier modLoc(String path) {
		return Identifier.fromNamespaceAndPath(MelonMod.MODID, path);
	}
}

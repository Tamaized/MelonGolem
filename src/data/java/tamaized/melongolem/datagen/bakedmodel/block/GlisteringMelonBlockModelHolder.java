package tamaized.melongolem.datagen.bakedmodel.block;

import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.bakedmodel.block.overlay.OverlaySideBlockModelHolder;

@Component
public class GlisteringMelonBlockModelHolder extends BlockModelHolder {

	@Autowired
	private OverlaySideBlockModelHolder overlaySideBlockModelHolder;

	public void build(BlockModelProvider provider) {
		overlaySideBlockModelHolder.get().ifPresent(parent -> set(
			provider.withExistingParent("block/glistering_melon", parent.getLocation())
				.renderType(RenderType.cutout().name)
				.texture("end", provider.modLoc("block/glistening_melon_top"))
				.texture("side", provider.modLoc("block/glistening_melon_side"))
				.texture("overlay-end", provider.modLoc("block/glistening_melon_top_overlay"))
				.texture("overlay-side", provider.modLoc("block/glistening_melon_side_overlay"))
		));
	}

}

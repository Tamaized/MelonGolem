package tamaized.melongolem.datagen.assets.bakedmodel.block.overlay;

import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.assets.bakedmodel.block.BlockModelHolder;

@Component
public class OverlaySideBlockModelHolder extends BlockModelHolder {

	@Autowired
	private OverlayBaseBlockModelHolder overlayBaseBlockModelHolder;

	public void build(BlockModelProvider provider) {
		overlayBaseBlockModelHolder.get().ifPresent(parent -> set(
			provider.withExistingParent("block/overlay/side", parent.getLocation())
				.texture("particle", "#side")
				.texture("down", "#end")
				.texture("up", "#end")
				.texture("north", "#side")
				.texture("east", "#side")
				.texture("south", "#side")
				.texture("west", "#side")
				.texture("overlay-down", "#overlay-end")
				.texture("overlay-up", "#overlay-end")
				.texture("overlay-north", "#overlay-side")
				.texture("overlay-east", "#overlay-side")
				.texture("overlay-south", "#overlay-side")
				.texture("overlay-west", "#overlay-side")
		));
	}

}

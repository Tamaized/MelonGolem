package tamaized.melongolem.datagen.assets.bakedmodel;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;

@Component
public class ModelProviderFactory {

	@Autowired
	private BlockModelProviderFactory blockModelProviderFactory;

	@Autowired
	private ItemModelProviderFactory itemModelProviderFactory;

	public ModelProvider make(GatherDataEvent event) {
		return new ModelProvider(
			event.getGenerator().getPackOutput(),
			MelonMod.MODID
		) {
			@Override
			protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
				blockModelProviderFactory.make(blockModels);
				itemModelProviderFactory.make(itemModels);
			}
		};
	}
}

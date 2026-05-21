package tamaized.melongolem.datagen.assets.bakedmodel;

import tamaized.beanification.Bean;
import tamaized.beanification.Directory;
import tamaized.datagenutil.assets.bakedmodel.block.BlockModelHolder;
import tamaized.datagenutil.assets.bakedmodel.block.BlockModelProviderFactory;
import tamaized.datagenutil.assets.bakedmodel.item.ItemModelHolder;
import tamaized.datagenutil.assets.bakedmodel.item.ItemModelProviderFactory;

import java.util.List;

public class ModelBeanProviders {

	@Bean
	private static BlockModelProviderFactory blockModelProviderFactory(
		@Directory(BlockModelHolder.class) List<BlockModelHolder> blockModelHolders
	) {
		return new BlockModelProviderFactory(blockModelHolders);
	}

	@Bean
	private static ItemModelProviderFactory itemModelProviderFactory(
		@Directory(ItemModelHolder.class) List<ItemModelHolder> itemModelHolders
	) {
		return new ItemModelProviderFactory(itemModelHolders);
	}

}

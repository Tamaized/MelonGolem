package tamaized.melongolem.datagen.assets.bakedmodel.block;

import tamaized.beanification.Bean;
import tamaized.beanification.Directory;
import tamaized.datagenutil.assets.bakedmodel.BlockModelHolder;
import tamaized.datagenutil.assets.bakedmodel.BlockModelProviderFactory;

import java.util.List;

public class BlockModelBeanProvider {

	@Bean
	private static BlockModelProviderFactory blockModelProviderFactory(
		@Directory(BlockModelHolder.class) List<BlockModelHolder> blockModelHolders
	) {
		return new BlockModelProviderFactory(blockModelHolders);
	}

}

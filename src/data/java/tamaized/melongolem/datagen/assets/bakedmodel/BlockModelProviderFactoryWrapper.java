package tamaized.melongolem.datagen.assets.bakedmodel;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.neoforged.neoforge.common.data.LanguageProvider;
import tamaized.beanification.*;
import tamaized.datagenutil.assets.bakedmodel.BlockModelHolder;
import tamaized.datagenutil.assets.bakedmodel.BlockModelProviderFactory;

import java.util.List;

@Component
public class BlockModelProviderFactoryWrapper {

	@Directory(BlockModelHolder.class)
	private List<BlockModelHolder> blockModelHolders;

	@PostConstructImplicit
	private BlockModelProviderFactory factory;

	@PostConstruct
	private void setup() {
		factory = new BlockModelProviderFactory(blockModelHolders);
	}

	public void make(BlockModelGenerators provider) {
		factory.make(provider);
	}

	public void lang(LanguageProvider provider) {
		factory.addLangEntries(provider);
	}

}

package tamaized.melongolem.datagen.generator;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.datagen.assets.bakedmodel.ModelProviderFactory;
import tamaized.melongolem.datagen.assets.lang.LangProviderFactory;

@Component
public class AssetsGenerator {

	@Autowired
	private ModelProviderFactory modelProviderFactory;

	@Autowired
	private LangProviderFactory langProviderFactory;

	public void generate(GatherDataEvent.Client event) {
		event.getGenerator().addProvider(true, modelProviderFactory.make(event));
		event.getGenerator().addProvider(true, langProviderFactory.make(event));
	}

}

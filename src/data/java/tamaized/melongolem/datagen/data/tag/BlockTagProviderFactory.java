package tamaized.melongolem.datagen.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.datagen.RegistryProvider;

@Component
public class BlockTagProviderFactory {

	@Autowired
	private RegistryProvider registryProvider;

	@Autowired
	private GlisteringMelonBlockTagFactory glisteringMelonBlockTagFactory;

	public TagsProvider<Block> make(GatherDataEvent event) {
		return new TagsProvider<>(
			event.getGenerator().getPackOutput(),
			Registries.BLOCK,
			registryProvider.retrieve(event),
			MelonMod.MODID
		) {
			@Override
			protected void addTags(HolderLookup.Provider provider) {
				glisteringMelonBlockTagFactory.make(this::tag);
			}
		};
	}

}

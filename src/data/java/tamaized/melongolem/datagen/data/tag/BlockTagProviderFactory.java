package tamaized.melongolem.datagen.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.HolderTagProvider;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.data.tags.TagAppender;
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

	public TagsProvider<Block> make(GatherDataEvent.Server event) {
		return new KeyTagProvider<>(
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

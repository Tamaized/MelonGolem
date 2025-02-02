package tamaized.melongolem.datagen.data.tag;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.registry.ModBlocks;

import java.util.function.Function;

@Component
public class GlisteringMelonBlockTagFactory {

	@Autowired
	private ModBlocks blocks;

	public void make(Function<TagKey<Block>, TagsProvider.TagAppender<Block>> tag) {
		tag.apply(BlockTags.MINEABLE_WITH_AXE).add(blocks.GLISTERING_MELON.unwrapKey().orElseThrow());
		tag.apply(BlockTags.SWORD_EFFICIENT).add(blocks.GLISTERING_MELON.unwrapKey().orElseThrow());
	}

}

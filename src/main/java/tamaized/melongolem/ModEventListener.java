package tamaized.melongolem;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.beanification.PostConstruct;
import tamaized.melongolem.common.EntityGlisteringMelonGolem;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.config.common.CommonConfig;
import tamaized.melongolem.registry.ModBlocks;

import java.util.Set;
import java.util.stream.StreamSupport;

@Component
public class ModEventListener {

	@Autowired
	private ModBlocks modBlocks;

	@Autowired
	private CommonConfig config;

	private final Lazy<Set<Block>> MELONS = Lazy.of(() -> ImmutableSet.of(
		Blocks.MELON,
		modBlocks.GLISTERING_MELON.get()
	));

	@PostConstruct(PostConstruct.Bus.GAME)
	public void init(IEventBus bus) {
		bus.addListener(PlayerInteractEvent.RightClickBlock.class, this::onRightClickBlock);
	}

	private void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		Player player = event.getEntity();
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);

		if (level.isClientSide())
			return;

		if (!config.compareStabbyItem(player.getItemInHand(InteractionHand.MAIN_HAND)) || !config.compareStabbyItem(player.getItemInHand(InteractionHand.OFF_HAND)))
			return;

		for (Block melonCheck : MELONS.get()) {
			if (state.is(melonCheck)) {
				if (level.getBlockState(pos.below()).getBlock() == melonCheck && level.getBlockState(pos.above()).getBlock() == melonCheck) {
					if (!player.isCreative()) {
						player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
						player.getItemInHand(InteractionHand.OFF_HAND).shrink(1);
					}
					level.removeBlock(pos.below(), false);
					level.removeBlock(pos, false);
					level.removeBlock(pos.above(), false);
					EntityMelonGolem melon = melonCheck == modBlocks.GLISTERING_MELON.get() ? new EntityGlisteringMelonGolem(level) : new EntityMelonGolem(level);
					melon.teleportTo(pos.getX() + 0.5F, pos.getY() - 0.5F, pos.getZ() + 0.5F);
					level.addFreshEntity(melon);
					break;
				}
			}
		}
	}

}

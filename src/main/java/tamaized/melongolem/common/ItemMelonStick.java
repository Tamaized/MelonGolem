package tamaized.melongolem.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import tamaized.beanification.Autowired;
import tamaized.beanification.Configurable;

import javax.annotation.Nonnull;

@Configurable
public class ItemMelonStick extends Item {

	@Autowired
	private PetHelper petHelper;

	public ItemMelonStick(Properties prop) {
		super(prop.durability(25));
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, @Nonnull InteractionHand hand) {
		player.swing(hand);
		if (level.isClientSide())
			return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
		if (level instanceof ServerLevel serverLevel) {
			if (petHelper.summon(serverLevel, player))
				player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
			else
				level.playSound(null, player.blockPosition(), SoundEvents.SLIME_BLOCK_BREAK, SoundSource.PLAYERS, 0.5F, player.getRandom().nextFloat() + 0.5F);
		}
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
	}
}

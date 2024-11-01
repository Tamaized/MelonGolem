package tamaized.melongolem.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import tamaized.beanification.Autowired;
import tamaized.beanification.Configurable;
import tamaized.melongolem.network.client.ClientPacketSendParticles;
import tamaized.melongolem.registry.ModDataAttachments;

import javax.annotation.Nonnull;
import java.util.Optional;

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
		if (level instanceof ServerLevel serverLevel)
			petHelper.summon(serverLevel, player);
		player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
	}
}

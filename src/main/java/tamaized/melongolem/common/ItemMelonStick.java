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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import tamaized.melongolem.network.client.ClientPacketSendParticles;
import tamaized.melongolem.registry.ModDataAttachments;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ItemMelonStick extends Item {

	public ItemMelonStick(Properties prop) {
		super(prop.defaultDurability(25));
	}

	public static void summonPet(ServerLevel level, Player owner) {
		TinyGolemAttachment attachment = owner.getData(ModDataAttachments.TINY_GOLEM);

		EntityTinyMelonGolem pet = attachment.getPet().orElse(new EntityTinyMelonGolem(level));
		pet.tame(owner);

		findTeleportFriendlyBlock(level, pet, owner.blockPosition()).ifPresent(pos -> {
			pet.moveTo(pos.getCenter());
			ClientPacketSendParticles particles = new ClientPacketSendParticles();
			for (int i = 0; i < 25; i++) {
				Vec3 result = pet.getViewVector(1F).yRot(pet.getRandom().nextFloat() * 360F).xRot(pet.getRandom().nextFloat() * 360F).scale(0.35F);
				particles.queueParticle(ParticleTypes.END_ROD, false, pet.getX() + result.x, pet.getY() + pet.getBbHeight() / 2F + result.y, pet.getZ() + result.z, 0, 0, 0);
			}
			PacketDistributor.TRACKING_CHUNK.with(level.getChunkAt(owner.blockPosition())).send(particles);
			if (attachment.getPet().isEmpty())
				level.addFreshEntity(pet);
			attachment.changePet(pet);
			level.playSound(null, pet.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, pet.getRandom().nextFloat() + 0.5F);
		});
	}

	public static Optional<BlockPos> findTeleportFriendlyBlock(Level world, Entity entity, BlockPos blockPos) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int l = world.getRandom().nextInt(6); l <= 8; ++l) {
			for (int i1 = world.getRandom().nextInt(6); i1 <= 8; ++i1) {
				for (int j = 3; j > -3; j--) {
					pos.set(blockPos.getX() + l, (blockPos.getY() + j) - 1, blockPos.getZ() + i1);
					BlockState iblockstate = world.getBlockState(pos);
					if (Block.canSupportCenter(world, pos, Direction.UP) && iblockstate.isValidSpawn(world, pos, entity.getType()) && world.isEmptyBlock(pos.above()) && world.isEmptyBlock(pos.above(2)))
						return Optional.of(pos.above());
				}
			}
		}
		return Optional.empty();
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, @Nonnull InteractionHand hand) {
		player.swing(hand);
		if (level.isClientSide())
			return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
		if (level instanceof ServerLevel serverLevel)
			summonPet(serverLevel, player);
		player.getItemInHand(hand).hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
	}
}

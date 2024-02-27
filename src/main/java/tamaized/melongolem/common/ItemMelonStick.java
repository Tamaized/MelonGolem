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
import tamaized.melongolem.common.capability.TinyGolemAttachment;
import tamaized.melongolem.network.client.ClientPacketSendParticles;
import tamaized.melongolem.registry.ModDataAttachments;

import javax.annotation.Nonnull;

public class ItemMelonStick extends Item {

	public ItemMelonStick(Properties prop) {
		super(prop.defaultDurability(25));
	}

	public static void summonPet(ServerLevel level, Player owner) {
		TinyGolemAttachment attachment = owner.getData(ModDataAttachments.TINY_GOLEM);

		boolean shouldSpawn = attachment.getPet().isEmpty();
		EntityTinyMelonGolem pet = attachment.getPet().orElse(new EntityTinyMelonGolem(level));
		pet.tame(owner);
		attachment.changePet(pet);

		int x = Mth.floor(owner.getX()) - 2;
		int z = Mth.floor(owner.getZ()) - 2;
		int y = Mth.floor(owner.getBoundingBox().minY);

		loop:
		for (int l = pet.getRandom().nextInt(6); l <= 8; ++l) {
			for (int i1 = pet.getRandom().nextInt(6); i1 <= 8; ++i1) {
				for (int j = 3; j > -3; j--) {
					if (isTeleportFriendlyBlock(level, pet, x, z, y + j, l, i1)) {
						double posx = (float) (x + l) + 0.5F;
						double posy = (double) y + j;
						double posz = (float) (z + i1) + 0.5F;
						pet.moveTo(posx, posy, posz);
						ClientPacketSendParticles particles = new ClientPacketSendParticles();
						for (int i = 0; i < 25; i++) {
							Vec3 result = pet.getViewVector(1F).yRot(pet.getRandom().nextFloat() * 360F).xRot(pet.getRandom().nextFloat() * 360F).scale(0.35F);
							particles.queueParticle(ParticleTypes.END_ROD, false, pet.getX() + result.x, pet.getY() + pet.getBbHeight() / 2F + result.y, pet.getZ() + result.z, 0, 0, 0);
						}
						PacketDistributor.TRACKING_CHUNK.with(level.getChunkAt(BlockPos.containing(x, y, z))).send(particles);
						if (shouldSpawn)
							level.addFreshEntity(pet);
						level.playSound(null, pet.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, pet.getRandom().nextFloat() + 0.5F);
						break loop;
					}
				}
			}
		}
	}

	private static boolean isTeleportFriendlyBlock(Level world, Entity entity, int x, int z, int y, int xOffset, int zOffset) {
		BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
		BlockState iblockstate = world.getBlockState(blockpos);
		return Block.canSupportCenter(world, blockpos, Direction.UP) && iblockstate.isValidSpawn(entity.level(), entity.blockPosition(), entity.getType()) && world.isEmptyBlock(blockpos.above()) && world.isEmptyBlock(blockpos.above(2));
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

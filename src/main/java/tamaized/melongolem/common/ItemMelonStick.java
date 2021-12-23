package tamaized.melongolem.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
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
import net.minecraftforge.network.PacketDistributor;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.capability.CapabilityList;
import tamaized.melongolem.network.client.ClientPacketHandlerParticle;

import javax.annotation.Nonnull;

public class ItemMelonStick extends Item {

	public ItemMelonStick(Properties prop) {
		super(prop.defaultDurability(25));
	}

	public static void summonPet(Level world, Player owner) {
		summonPet(world, owner, null);
	}

	public static void summonPet(Level world, Player owner, EntityTinyMelonGolem golem) {
		if (golem != null && !golem.isAlive())
			return;
		owner.getCapability(CapabilityList.TINY_GOLEM).ifPresent(cap -> {
			if (cap.load(true)) {
				if (!(world instanceof ServerLevel) || owner.level.getServer() == null)
					return;
				ServerLevel last = owner.level.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, cap.getLoadDim()));
				if (last != null && cap.getLoadPos() != null) {
					last.getBlockState(cap.getLoadPos()); // Ensure chunk is loaded
					Entity entity = last.getEntity(cap.getLoadPetID());
					if (entity instanceof EntityTinyMelonGolem melon) {
						if (!world.dimension().location().equals(last.dimension().location()))
							melon.changeDimension((ServerLevel) world);
						summonPet(world, owner, melon);
						return;
					}
				}
			}
			EntityTinyMelonGolem oldPet = golem != null ? golem : cap.getPet();
			if (oldPet != null && !oldPet.isAlive())
				oldPet = null;
			EntityTinyMelonGolem pet = oldPet == null ? new EntityTinyMelonGolem(world) : oldPet;
			pet.tame(owner);
			if (oldPet == null)
				cap.setPet(pet);

			int x = Mth.floor(owner.getX()) - 2;
			int z = Mth.floor(owner.getZ()) - 2;
			int y = Mth.floor(owner.getBoundingBox().minY);

			loop:
			for (int l = pet.getRandom().nextInt(6); l <= 8; ++l) {
				for (int i1 = pet.getRandom().nextInt(6); i1 <= 8; ++i1) {
					for (int j = 3; j > -3; j--) {
						if (isTeleportFriendlyBlock(world, pet, x, z, y + j, l, i1)) {
							double posx = (float) (x + l) + 0.5F;
							double posy = (double) y + j;
							double posz = (float) (z + i1) + 0.5F;
							pet.teleportTo(posx, posy, posz);
							if (!pet.level.dimension().location().equals(owner.level.dimension().location())) {
								pet = (EntityTinyMelonGolem) pet.changeDimension((ServerLevel) owner.level);
								cap.setPet(pet);
								if (pet == null)
									return;
								pet.teleportTo(posx, posy, posz);
							}
							for (int i = 0; i < 25; i++) {
								Vec3 result = pet.getViewVector(1F).yRot(pet.getRandom().nextFloat() * 360F).xRot(pet.getRandom().nextFloat() * 360F).scale(0.35F);
								spawnVanillaParticleOnServer(world, ParticleTypes.END_ROD, pet.getX() + result.x, pet.getY() + pet.getBbHeight() / 2F + result.y, pet.getZ() + result.z, 0, 0, 0);
							}
							if (oldPet == null)
								world.addFreshEntity(pet);
							world.playSound(null, pet.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, pet.getRandom().nextFloat() + 0.5F);
							break loop;
						}
					}
				}
			}
		});
	}

	public static void spawnVanillaParticleOnServer(Level world, ParticleOptions particle, double x, double y, double z, double xS, double yS, double zS) {
		MelonMod.network.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(new BlockPos(x, y, z))), new ClientPacketHandlerParticle(particle.getType().getRegistryName(), new Vec3(x, y, z), new Vec3(xS, yS, zS)));
	}

	private static boolean isTeleportFriendlyBlock(Level world, Entity entity, int x, int z, int y, int xOffset, int zOffset) {
		BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
		BlockState iblockstate = world.getBlockState(blockpos);
		return Block.canSupportCenter(world, blockpos, Direction.UP) && iblockstate.isValidSpawn(entity.level, entity.blockPosition(), entity.getType()) && world.isEmptyBlock(blockpos.above()) && world.isEmptyBlock(blockpos.above(2));
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
		playerIn.swing(handIn);
		if (worldIn.isClientSide)
			return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
		summonPet(worldIn, playerIn);
		playerIn.getItemInHand(handIn).hurtAndBreak(1, playerIn, e -> e.broadcastBreakEvent(handIn));
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
	}
}

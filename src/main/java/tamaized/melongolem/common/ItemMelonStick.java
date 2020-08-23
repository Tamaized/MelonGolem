package tamaized.melongolem.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.capability.CapabilityList;
import tamaized.melongolem.network.client.ClientPacketHandlerParticle;

import javax.annotation.Nonnull;

public class ItemMelonStick extends Item {

	public ItemMelonStick(Properties prop) {
		super(prop.defaultMaxDamage(25));
	}

	public static void summonPet(World world, PlayerEntity owner) {
		summonPet(world, owner, null);
	}

	public static void summonPet(World world, PlayerEntity owner, EntityTinyMelonGolem golem) {
		if (golem != null && !golem.isAlive())
			return;
		owner.getCapability(CapabilityList.TINY_GOLEM).ifPresent(cap -> {
			if (cap.load(true)) {
				if (!(world instanceof ServerWorld) || owner.world.getServer() == null)
					return;
				ServerWorld last = owner.world.getServer().getWorld(RegistryKey.func_240903_a_(Registry.WORLD_KEY, cap.getLoadDim()));
				if (last != null && cap.getLoadPos() != null) {
					last.getBlockState(cap.getLoadPos()); // Ensure chunk is loaded
					Entity entity = last.getEntityByUuid(cap.getLoadPetID());
					if (entity instanceof EntityTinyMelonGolem) {
						EntityTinyMelonGolem melon = (EntityTinyMelonGolem) entity;
						if (!world.getDimensionKey().func_240901_a_().equals(last.getDimensionKey().func_240901_a_()))
							melon.changeDimension((ServerWorld) world);
						summonPet(world, owner, melon);
						return;
					}
				}
			}
			EntityTinyMelonGolem oldPet = golem != null ? golem : cap.getPet();
			if (oldPet != null && !oldPet.isAlive())
				oldPet = null;
			EntityTinyMelonGolem pet = oldPet == null ? new EntityTinyMelonGolem(world) : oldPet;
			pet.setTamedBy(owner);
			if (oldPet == null)
				cap.setPet(pet);

			int x = MathHelper.floor(owner.getPosX()) - 2;
			int z = MathHelper.floor(owner.getPosZ()) - 2;
			int y = MathHelper.floor(owner.getBoundingBox().minY);

			loop:
			for (int l = pet.getRNG().nextInt(6); l <= 8; ++l) {
				for (int i1 = pet.getRNG().nextInt(6); i1 <= 8; ++i1) {
					for (int j = 3; j > -3; j--) {
						if (isTeleportFriendlyBlock(world, pet, x, z, y + j, l, i1)) {
							double posx = (float) (x + l) + 0.5F;
							double posy = (double) y + j;
							double posz = (float) (z + i1) + 0.5F;
							pet.setPositionAndUpdate(posx, posy, posz);
							if (!pet.world.getDimensionKey().func_240901_a_().equals(owner.world.getDimensionKey().func_240901_a_())) {
								pet = (EntityTinyMelonGolem) pet.changeDimension((ServerWorld) owner.world);
								cap.setPet(pet);
								if (pet == null)
									return;
								pet.setPositionAndUpdate(posx, posy, posz);
							}
							for (int i = 0; i < 25; i++) {
								Vector3d result = pet.getLook(1F).rotateYaw(pet.getRNG().nextFloat() * 360F).rotatePitch(pet.getRNG().nextFloat() * 360F).scale(0.35F);
								spawnVanillaParticleOnServer(world, ParticleTypes.END_ROD, pet.getPosX() + result.x, pet.getPosY() + pet.getHeight() / 2F + result.y, pet.getPosZ() + result.z, 0, 0, 0);
							}
							if (oldPet == null)
								world.addEntity(pet);
							world.playSound(null, pet.getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0F, pet.getRNG().nextFloat() + 0.5F);
							break loop;
						}
					}
				}
			}
		});
	}

	public static void spawnVanillaParticleOnServer(World world, IParticleData particle, double x, double y, double z, double xS, double yS, double zS) {
		MelonMod.network.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(new BlockPos(x, y, z))), new ClientPacketHandlerParticle(particle.getType().getRegistryName(), new Vector3d(x, y, z), new Vector3d(xS, yS, zS)));
	}

	private static boolean isTeleportFriendlyBlock(World world, Entity entity, int x, int z, int y, int xOffset, int zOffset) {
		BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
		BlockState iblockstate = world.getBlockState(blockpos);
		return Block.hasEnoughSolidSide(world, blockpos, Direction.UP) && iblockstate.canEntitySpawn(entity.world, entity.getPosition(), entity.getType()) && world.isAirBlock(blockpos.up()) && world.isAirBlock(blockpos.up(2));
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
		playerIn.swingArm(handIn);
		if (worldIn.isRemote)
			return new ActionResult<>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
		summonPet(worldIn, playerIn);
		playerIn.getHeldItem(handIn).damageItem(1, playerIn, e -> e.sendBreakAnimation(handIn));
		return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
	}
}

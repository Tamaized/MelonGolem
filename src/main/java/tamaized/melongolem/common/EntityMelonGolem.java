package tamaized.melongolem.common;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.client.ClientUtil;
import tamaized.melongolem.network.client.ClientPacketMelonAmbientSound;
import tamaized.melongolem.registry.ModBlocks;
import tamaized.melongolem.registry.ModEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class EntityMelonGolem extends AbstractGolem implements RangedAttackMob, IShearable, ISignHolder {

	private static final EntityDataAccessor<ItemStack> HEAD = SynchedEntityData.defineId(EntityMelonGolem.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Boolean> GLOWING_TEXT = SynchedEntityData.defineId(EntityMelonGolem.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> TEXT_COLOR = SynchedEntityData.defineId(EntityMelonGolem.class, EntityDataSerializers.INT);
	private static final List<EntityDataAccessor<Component>> SIGN_TEXT = Lists.newArrayList(

			SynchedEntityData.defineId(EntityMelonGolem.class, EntityDataSerializers.COMPONENT),

			SynchedEntityData.defineId(EntityMelonGolem.class, EntityDataSerializers.COMPONENT),

			SynchedEntityData.defineId(EntityMelonGolem.class, EntityDataSerializers.COMPONENT),

			SynchedEntityData.defineId(EntityMelonGolem.class, EntityDataSerializers.COMPONENT)

	);
	private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(EntityMelonGolem.class, EntityDataSerializers.FLOAT);
	public static BlockState SIGN_TILE_BLOCKSTATE = Blocks.OAK_WALL_SIGN.defaultBlockState();
	public static final SignBlockEntity te = new SignBlockEntity(BlockPos.ZERO, SIGN_TILE_BLOCKSTATE) {
		@Nonnull
		@Override
		public BlockState getBlockState() {
			return SIGN_TILE_BLOCKSTATE;
		}
		@Nonnull
		@Override
		public BlockPos getBlockPos() {
			return FMLEnvironment.dist == Dist.CLIENT && Minecraft.getInstance().getCameraEntity() != null ?
					Minecraft.getInstance().getCameraEntity().blockPosition() :
					this.worldPosition;
		}
	};

	public EntityMelonGolem(Level level) {
		this(ModEntities.MELON_GOLEM.get(), level);
	}

	public EntityMelonGolem(EntityType<? extends EntityMelonGolem> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().
				add(Attributes.MAX_HEALTH, 8.0F).
				add(Attributes.MOVEMENT_SPEED, 0.2F);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(HEAD, ItemStack.EMPTY);
		entityData.define(GLOWING_TEXT, false);
		entityData.define(TEXT_COLOR, DyeColor.BLACK.getId());
		for (EntityDataAccessor<Component> sign : SIGN_TEXT)
			entityData.define(sign, Component.literal(""));
		entityData.define(PITCH, getRandom().nextFloat() * 3.0F);
	}

	@Override
	public void setSignText(int index, Component text) {
		entityData.set(SIGN_TEXT.get(index), text);
	}

	@Override
	public int networkID() {
		return getId();
	}

	@Override
	public boolean glowingText() {
		return getEntityData().get(GLOWING_TEXT);
	}

	@Override
	public DyeColor getTextColor() {
		return DyeColor.byId(getEntityData().get(TEXT_COLOR));
	}

	@Override
	public Component getSignText(int index) {
		return entityData.get(SIGN_TEXT.get(index));
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
		this.goalSelector.addGoal(2, new EntityAISearchAndEatMelons(this));
		this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D, 1.0000001E-5F));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, (e) -> e instanceof Enemy));
	}

	@Override
	public void performRangedAttack(@Nonnull LivingEntity target, float distanceFactor) {
		EntityMelonSlice slice = new EntityMelonSlice(this.level(), this);
		double d0 = target.getY() + (double) target.getEyeHeight() - 1.100000023841858D;
		double d1 = target.getX() - this.getX();
		double d2 = d0 - slice.getY();
		double d3 = target.getZ() - this.getZ();
		double f = Math.sqrt(d1 * d1 + d3 * d3) * 0.2F;
		slice.shoot(d1, d2 + f, d3, 1.6F, 12.0F);
		this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		slice.teleportTo(slice.getX(), slice.getY(), slice.getZ());
		level().addFreshEntity(slice);
	}

	@Override
	public boolean isShearable(@Nonnull ItemStack item, Level world, BlockPos vertex) {
		return !getHead().isEmpty();
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.SLIME_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.SLIME_DEATH;
	}

	@Override
	public int getAmbientSoundInterval() {
		return getHead().is(ItemTags.SIGNS) ? 200 : super.getAmbientSoundInterval();
	}

	@Override
	public float getVoicePitch() { // protected -> public
		return super.getVoicePitch();
	}

	@Override
	public float getSoundVolume() { // protected -> public
		return super.getSoundVolume();
	}

	@Override
	public void playAmbientSound() {
		if (!level().isClientSide())
			PacketDistributor.TRACKING_ENTITY.with(this).send(new ClientPacketMelonAmbientSound(this));
	}

	@Nonnull
	@Override
	public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
		if (!MelonMod.config.hats.get() || player.getMainHandItem().getItem() instanceof ShearsItem || player.getOffhandItem().getItem() instanceof ShearsItem)
			return InteractionResult.FAIL;
		ItemStack stack = player.getItemInHand(hand);
		if (!stack.isEmpty() && getHead().isEmpty()) {
			if (Block.byItem(stack.getItem()) != Blocks.AIR || stack.is(ItemTags.SIGNS)) {
				setHead(stack);
				if (!player.isCreative())
					player.getItemInHand(hand).shrink(1);
				return InteractionResult.SUCCESS;
			}
		} else if (!getHead().isEmpty() && getHead().is(ItemTags.SIGNS)) {
			if (stack.is(Items.GLOW_INK_SAC) && !getEntityData().get(GLOWING_TEXT)) {
				getEntityData().set(GLOWING_TEXT, true);
				playSound(SoundEvents.GLOW_INK_SAC_USE);
				if (!player.isCreative())
					player.getItemInHand(hand).shrink(1);
			} else if (stack.is(Items.INK_SAC) && getEntityData().get(GLOWING_TEXT)) {
				getEntityData().set(GLOWING_TEXT, false);
				playSound(SoundEvents.INK_SAC_USE);
				if (!player.isCreative())
					player.getItemInHand(hand).shrink(1);
			} else if (stack.getItem() instanceof DyeItem dye && getTextColor() != dye.getDyeColor()) {
				getEntityData().set(TEXT_COLOR, dye.getDyeColor().getId());
				playSound(SoundEvents.DYE_USE);
				if (!player.isCreative())
					player.getItemInHand(hand).shrink(1);
			} else {
				if (level().isClientSide()) {
					if (getHead().is(ItemTags.SIGNS) && distanceTo(player) <= 6)
						ClientUtil.openGolemSignScreen(this);
				}
			}
			return InteractionResult.sidedSuccess(level().isClientSide());
		}
		return InteractionResult.FAIL;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return 1.7F;
	}

	@Override
	public ItemStack getHead() {
		return entityData.get(HEAD);
	}

	public void setHead(ItemStack stack) {
		for (int i = 0; i < 4; i++)
			setSignText(i, Component.literal(""));
		ItemStack newstack = stack.copy();
		newstack.setCount(1);
		entityData.set(HEAD, newstack);
	}

	@Nonnull
	@Override
	public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos vertex, int fortune) {
		List<ItemStack> list = Lists.newArrayList(MelonMod.config.shear.get() ? getHead() : ItemStack.EMPTY);
		setHead(ItemStack.EMPTY);
		return list;
	}

	@Override
	public void die(@Nonnull DamageSource cause) {
		super.die(cause);
		ItemStack stack = getHead();
		if (!level().isClientSide() && !stack.isEmpty()) {
			ItemEntity e = new ItemEntity(level(), getX(), getY(), getZ(), stack);
			e.setDeltaMovement(e.getDeltaMovement().add(

					getRandom().nextFloat() * 0.05F,

					(getRandom().nextFloat() - getRandom().nextFloat()) * 0.1F,

					(getRandom().nextFloat() - getRandom().nextFloat()) * 0.1F

			));
			level().addFreshEntity(e);
		}
	}

	@Nonnull
	@Override
	public CompoundTag saveWithoutId(CompoundTag compound) {
		compound.put("head", getHead().save(new CompoundTag()));
		compound.putBoolean("glowingText", glowingText());
		compound.putInt("textColor", getTextColor().getId());
		for (int i = 0; i < 4; i++) {
			String s = Component.Serializer.toJson(getSignText(i));
			compound.putString("Text" + (i + 1), s);
		}
		return super.saveWithoutId(compound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		setHead(ItemStack.of(compound.getCompound("head")));
		getEntityData().set(GLOWING_TEXT, compound.getBoolean("glowingText"));
		getEntityData().set(TEXT_COLOR, compound.getInt("textColor"));
		for (int i = 0; i < 4; i++) {
			String s = compound.getString("Text" + (i + 1));
			Component itextcomponent = Component.Serializer.fromJson(s);

			try {
				setSignText(i, itextcomponent == null ? Component.literal("") : ComponentUtils.updateForEntity(createCommandSourceStack(), itextcomponent, null, 0));
			} catch (CommandSyntaxException var7) {
				setSignText(i, itextcomponent);
			}
		}
	}

	public float getPitch() {
		return entityData.get(PITCH);
	}

	static class EntityAISearchAndEatMelons extends Goal {

		private final Mob parent;
		private final Item melon;
		private final Block melonblock;
		private int cooldown;
		private final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
		private boolean foundMelon = false;

		EntityAISearchAndEatMelons(Mob entity) {
			parent = entity;
			setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
			melon = entity instanceof EntityGlisteringMelonGolem ? Items.GLISTERING_MELON_SLICE : Items.MELON_SLICE;
			melonblock = entity instanceof EntityGlisteringMelonGolem ? ModBlocks.GLISTERING_MELON.get() : Blocks.MELON;
		}

		@Override
		public boolean canUse() {
			return MelonMod.config.eats.get() && parent.getHealth() < parent.getMaxHealth();
		}

		@Override
		public void stop() {
			cooldown = 0;
		}

		private boolean isMelon(ItemEntity item) {
			return isMelon(item.getItem());
		}

		private boolean isMelon(ItemStack item) {
			return isMelon(item.getItem());
		}

		private boolean isMelon(Item item) {
			return item == melon || item == melonblock.asItem();
		}

		@Override
		public void tick() {
			if (parent == null)
				return;
			if (cooldown > 0)
				cooldown--;
			final int radius = 25;
			AABB area = new AABB(parent.getX() - radius, parent.getY() - radius, parent.getZ() - radius, parent.getX() + radius, parent.getY() + radius, parent.getZ() + radius);
			List<ItemEntity> items = parent.level().getEntitiesOfClass(ItemEntity.class, area);
			for (ItemEntity item : items) {
				if (parent.getNavigation().isDone() && isMelon(item)) {
					parent.getNavigation().moveTo(item, 1.25F);
					parent.getLookControl().setLookAt(item, 30.0F, 30.0F);
				}
				if (cooldown <= 0 && item.isAlive() && isMelon(item) && item.getBoundingBox().intersects(parent.getBoundingBox().inflate(1))) {
					boolean flag = item.getItem().getItem() == melonblock.asItem();
					item.getItem().shrink(1);
					parent.playSound(SoundEvents.PLAYER_BURP, 1F, 1F);
					parent.heal(MelonMod.config.heal.get().floatValue() * (flag ? 9 : 1));
					cooldown = 30 + parent.getRandom().nextInt(40);
				}
			}
			if (parent.getNavigation().isDone()) {
				foundMelon = false;
				search:
				for (int x = -radius; x < radius; x++)
					for (int y = -radius; y < radius; y++)
						for (int z = -radius; z < radius; z++) {
							mutableBlockPos.set(parent.blockPosition().getX() + x, parent.blockPosition().getY() + y, parent.blockPosition().getZ() + z);
							if (parent.level().hasChunk(SectionPos.blockToSectionCoord(mutableBlockPos.getX()), SectionPos.blockToSectionCoord(mutableBlockPos.getZ()))) {
								BlockEntity te = parent.level().getBlockEntity(mutableBlockPos);
								if (te != null) {
									IItemHandler cap = parent.level().getCapability(Capabilities.ItemHandler.BLOCK, mutableBlockPos, Direction.UP);
									if (cap != null) {
										for (int i = 0; i < cap.getSlots(); i++) {
											if (isMelon(cap.getStackInSlot(i))) {
												foundMelon = parent.distanceToSqr(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ()) < 4 || parent.getNavigation().moveTo(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ(), 1.25F);
												break;
											}
										}
									}
									if (foundMelon)
										break search;
								}
							}
						}
			}
			if (foundMelon) {
				// Validate
				if (!parent.level().hasChunk(SectionPos.blockToSectionCoord(mutableBlockPos.getX()), SectionPos.blockToSectionCoord(mutableBlockPos.getZ()))) {
					parent.getNavigation().stop();
					foundMelon = false;
					return;
				}
				BlockEntity te = parent.level().getBlockEntity(mutableBlockPos);
				if (te == null || parent.level().getCapability(Capabilities.ItemHandler.BLOCK, mutableBlockPos, Direction.UP) == null) {
					parent.getNavigation().stop();
					foundMelon = false;
					return;
				}
				IItemHandler handler = parent.level().getCapability(Capabilities.ItemHandler.BLOCK, mutableBlockPos, Direction.UP);
				if (handler != null) {
					boolean valid = false;
					int i;
					for (i = 0; i < handler.getSlots(); i++) {
						if (isMelon(handler.getStackInSlot(i))) {
							valid = true;
							break;
						}
					}
					if (!valid) {
						parent.getNavigation().stop();
						foundMelon = false;
						return;
					}

					if (cooldown <= 0 && parent.distanceToSqr(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ()) < 4) {
						boolean flag = handler.getStackInSlot(i).getItem() == melonblock.asItem();
						handler.getStackInSlot(i).shrink(1);
						parent.playSound(SoundEvents.PLAYER_BURP, 1F, 1F);
						parent.heal(MelonMod.config.heal.get().floatValue() * (flag ? 9 : 1));
						cooldown = 10 + parent.getRandom().nextInt(40);
					}
				} else {
					parent.getNavigation().stop();
					foundMelon = false;
				}
			}
		}
	}
}

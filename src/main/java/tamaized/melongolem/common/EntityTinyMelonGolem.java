package tamaized.melongolem.common;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.IShearable;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.client.ClientUtil;
import tamaized.melongolem.common.capability.TinyGolemAttachment;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.registry.ModDataAttachments;
import tamaized.melongolem.registry.ModEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class EntityTinyMelonGolem extends TamableAnimal implements IShearable, ISignHolder {

	private static final EntityDataAccessor<ItemStack> HEAD = SynchedEntityData.defineId(EntityTinyMelonGolem.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Boolean> ENABLED = SynchedEntityData.defineId(EntityTinyMelonGolem.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(EntityTinyMelonGolem.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> GLOWING_TEXT = SynchedEntityData.defineId(EntityTinyMelonGolem.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> TEXT_COLOR = SynchedEntityData.defineId(EntityTinyMelonGolem.class, EntityDataSerializers.INT);
	private static final List<EntityDataAccessor<Component>> SIGN_TEXT = Lists.newArrayList(

			SynchedEntityData.defineId(EntityTinyMelonGolem.class, EntityDataSerializers.COMPONENT),

			SynchedEntityData.defineId(EntityTinyMelonGolem.class, EntityDataSerializers.COMPONENT),

			SynchedEntityData.defineId(EntityTinyMelonGolem.class, EntityDataSerializers.COMPONENT),

			SynchedEntityData.defineId(EntityTinyMelonGolem.class, EntityDataSerializers.COMPONENT)

	);

	public EntityTinyMelonGolem(Level level) {
		this(ModEntities.TINY_MELON_GOLEM.get(), level);
	}

	public EntityTinyMelonGolem(EntityType<EntityTinyMelonGolem> type, Level level) {
		super(type, level);
	}

	@Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
		return null;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(HEAD, ItemStack.EMPTY);
		entityData.define(GLOWING_TEXT, false);
		entityData.define(TEXT_COLOR, DyeColor.BLACK.getId());
		entityData.define(ENABLED, false);
		entityData.define(COLOR, 0xFFFFFF);
		for (EntityDataAccessor<Component> sign : SIGN_TEXT)
			entityData.define(sign, Component.literal(""));
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide() || !isAlive())
			return;
		LivingEntity owner = getOwner();
		if (owner == null || !owner.isAlive())
			return;
		if (DonatorHandler.donators.contains(getOwnerUUID())) {
			DonatorHandler.DonatorSettings settings = DonatorHandler.settings.get(getOwnerUUID());
			if (settings != null) {
				entityData.set(ENABLED, settings.enabled);
				entityData.set(COLOR, settings.color);
			}
		}
		TinyGolemAttachment attachment = owner.getData(ModDataAttachments.TINY_GOLEM);
		Optional<EntityTinyMelonGolem> pet = attachment.getPet();
		if (attachment.isLoaded() && pet.map(p -> p != this).orElse(true)) {
			hurt(level().damageSources().fellOutOfWorld(), 1024F);
		}
	}

	@Override
	public boolean isNoAi() {
		return super.isNoAi() || getOwner() == null;
	}

	public boolean isEnabled() {
		return entityData.get(ENABLED);
	}

	public int getColor() {
		return entityData.get(COLOR);
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
		goalSelector.addGoal(0, new FollowOwnerGoal(this, 1.0D, 4.0F, 2.0F, true));
		goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(2, new RandomLookAroundGoal(this));
	}

	@Override
	public boolean isShearable(@Nonnull ItemStack item, Level world, BlockPos vertex) {
		return !getHead().isEmpty();
	}

	@Override
	public float getVoicePitch() {
		return random.nextFloat() * 0.5F + 2F;
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
	public float getEyeHeightAccess(Pose pose, EntityDimensions dimensions) {
		return 0.425F;
	}

	@Override
	public ItemStack getHead() {
		return entityData.get(HEAD);
	}

	public void setHead(ItemStack stack) {
		for (int i = 0; i < 4; ++i)
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
		compound.putBoolean("donator_enabled", isEnabled());
		compound.putInt("donator_color", getColor());
		for (int i = 0; i < 4; ++i) {
			String s = Component.Serializer.toJson(getSignText(i));
			compound.putString("Text" + (i + 1), s);
		}
		return super.saveWithoutId(compound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		if (compound.contains("donator_enabled"))
			entityData.set(ENABLED, compound.getBoolean("donator_enabled"));
		if (compound.contains("donator_color"))
			entityData.set(COLOR, compound.getInt("donator_color"));
		setHead(ItemStack.of(compound.getCompound("head")));
		getEntityData().set(GLOWING_TEXT, compound.getBoolean("glowingText"));
		getEntityData().set(TEXT_COLOR, compound.getInt("textColor"));
		for (int i = 0; i < 4; ++i) {
			String s = compound.getString("Text" + (i + 1));
			Component itextcomponent = Component.Serializer.fromJson(s);

			try {
				setSignText(i, itextcomponent == null ? Component.literal("") : ComponentUtils.updateForEntity(createCommandSourceStack(), itextcomponent, null, 0));
			} catch (CommandSyntaxException var7) {
				setSignText(i, itextcomponent);
			}
		}
		super.readAdditionalSaveData(compound);
	}

}

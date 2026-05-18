package tamaized.melongolem.common;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.ResolutionContext;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.Lazy;
import tamaized.beanification.Autowired;
import tamaized.beanification.BeanContext;
import tamaized.beanification.Configurable;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.client.ClientUtil;
import tamaized.melongolem.config.common.CommonConfig;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.registry.ModDataAttachments;
import tamaized.melongolem.registry.ModEntities;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Configurable
public class EntityTinyMelonGolem extends TamableAnimal implements IShearable, ISignHolder {

	private static final Lazy<ModEntities> MOD_ENTITIES = BeanContext.injectLazy(ModEntities.class);

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

	@Autowired
	private DonatorHandler donatorHandler;

	@Autowired
	private CommonConfig config;

	@Autowired
	private ModDataAttachments modDataAttachments;

	public EntityTinyMelonGolem(Level level) {
		this(MOD_ENTITIES.get().TINY_MELON_GOLEM.get(), level);
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
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(HEAD, ItemStack.EMPTY);
		builder.define(GLOWING_TEXT, false);
		builder.define(TEXT_COLOR, DyeColor.BLACK.getId());
		builder.define(ENABLED, false);
		builder.define(COLOR, 0xFFFFFF);
		for (EntityDataAccessor<Component> sign : SIGN_TEXT)
			builder.define(sign, Component.literal(""));
	}

	@Override
	@SuppressWarnings("deprecation")
	public void tick() {
		super.tick();
		if (level().isClientSide() || !isAlive())
			return;
		if (getOwner() == null) {
			hurt(level().damageSources().fellOutOfWorld(), 1024F);
		}
		LivingEntity owner = getOwner();
		if (owner == null || !owner.isAlive())
			return;
		if (donatorHandler.isDonator(owner.getUUID())) {
			donatorHandler.getSettings(owner.getUUID()).ifPresent(settings -> {
				entityData.set(ENABLED, settings.enabled());
				entityData.set(COLOR, settings.color());
			});
		}
		TinyGolemAttachment attachment = owner.getData(modDataAttachments.TINY_GOLEM);
		Optional<EntityTinyMelonGolem> pet = attachment.getPet();
		if (attachment.isLoaded() && pet.map(p -> p != this).orElse(true)) {
			hurt(level().damageSources().fellOutOfWorld(), 1024F);
		}
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		if (getOwner() != null)
			super.customServerAiStep(level);
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
		goalSelector.addGoal(0, new FollowOwnerGoal(this, 1.0D, 4.0F, 2.0F));
		goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(2, new RandomLookAroundGoal(this));
	}

	@Override
	public boolean isShearable(@org.jetbrains.annotations.Nullable Player player, ItemStack item, Level level, BlockPos pos) {
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

	@Override
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 vec) {
		if (config.getHats().map(ModConfigSpec.BooleanValue::isFalse).orElse(true)
			|| player.getMainHandItem().getItem() instanceof ShearsItem
			|| player.getOffhandItem().getItem() instanceof ShearsItem
		)
			return InteractionResult.FAIL;
		// TODO abstract this into a static helper method in EntityMelonGolem
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
			} else if (stack.has(DataComponents.DYE) && getTextColor() != stack.get(DataComponents.DYE)) {
				getEntityData().set(TEXT_COLOR, Objects.requireNonNull(stack.get(DataComponents.DYE)).getId());
				playSound(SoundEvents.DYE_USE);
				if (!player.isCreative())
					player.getItemInHand(hand).shrink(1);
			} else {
				if (level().isClientSide()) {
					if (getHead().is(ItemTags.SIGNS) && distanceTo(player) <= 6)
						ClientUtil.openGolemSignScreen(this);
				}
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
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

	@Override
	public List<ItemStack> onSheared(@org.jetbrains.annotations.Nullable Player player, ItemStack item, Level level, BlockPos pos) {
		List<ItemStack> list = Collections.singletonList(config.getShear().map(ModConfigSpec.BooleanValue::isTrue).orElse(false) ? getHead() : ItemStack.EMPTY);
		setHead(ItemStack.EMPTY);
		return list;
	}

	@Override
	public void die(DamageSource cause) {
		super.die(cause);
		// TODO abstract this into a static helper method in EntityMelonGolem
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

	@Override
	public void saveWithoutId(ValueOutput compound) {
		compound.storeNullable("head", ItemStack.CODEC, getHead());
		compound.putBoolean("glowingText", glowingText());
		compound.putInt("textColor", getTextColor().getId());
		compound.putBoolean("donator_enabled", isEnabled());
		compound.putInt("donator_color", getColor());
		for (int i = 0; i < 4; i++) {
			compound.storeNullable("Text" + (i + 1), ComponentSerialization.CODEC, getSignText(i));
		}
		super.saveWithoutId(compound);
	}

	@Override
	public void readAdditionalSaveData(ValueInput compound) {
		super.readAdditionalSaveData(compound);
		entityData.set(ENABLED, compound.getBooleanOr("donator_enabled", false));
		entityData.set(COLOR, compound.getIntOr("donator_color", 0));
		setHead(compound.read("head", ItemStack.CODEC).orElse(ItemStack.EMPTY));
		getEntityData().set(GLOWING_TEXT, compound.getBooleanOr("glowingText", false));
		getEntityData().set(TEXT_COLOR, compound.getIntOr("textColor", 0));
		for (int i = 0; i < 4; i++) {
			Optional<Component> text = compound.read("Text" + (i + 1), ComponentSerialization.CODEC);
			Component component = Component.literal("");

			if (text.isPresent()) {
				try {
					if (this.level() instanceof ServerLevel server) {
						component = ComponentUtils.resolve(ResolutionContext.create(createCommandSourceStackForNameResolution(server)), text.get());
					}
				} catch (CommandSyntaxException error) {
					component = text.get();
				}
			}
			setSignText(i, component);
		}
	}

	@Override
	public boolean isFood(ItemStack itemStack) {
		return false;
	}

}

package tamaized.melongolem.common;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import tamaized.beanification.Autowired;
import tamaized.beanification.BeanContext;
import tamaized.beanification.Configurable;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.client.ClientUtil;
import tamaized.melongolem.config.common.CommonConfig;
import tamaized.melongolem.network.client.ClientPacketMelonAmbientSound;
import tamaized.melongolem.registry.ModBlocks;
import tamaized.melongolem.registry.ModEntities;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

@Configurable
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

	private final SignBlockEntity signTileEntity = new SignBlockEntity(BlockPos.ZERO, Blocks.OAK_WALL_SIGN.defaultBlockState()) {
		@Override
		public BlockPos getBlockPos() {
			return EntityMelonGolem.this.blockPosition();
		}

		@Override
		public Level getLevel() {
			return EntityMelonGolem.this.level();
		}

		@Override
		public boolean hasLevel() {
			return true;
		}
	};

	protected static final Lazy<ModEntities> MOD_ENTITIES = BeanContext.injectLazy(ModEntities.class);
	protected static final Lazy<ModBlocks> MOD_BLOCKS = BeanContext.injectLazy(ModBlocks.class);

	@Autowired
	private CommonConfig config;

	public EntityMelonGolem(Level level) {
		this(MOD_ENTITIES.get().MELON_GOLEM.get(), level);
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
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(HEAD, ItemStack.EMPTY);
		builder.define(GLOWING_TEXT, false);
		builder.define(TEXT_COLOR, DyeColor.BLACK.getId());
		for (EntityDataAccessor<Component> sign : SIGN_TEXT)
			builder.define(sign, Component.literal(""));
		builder.define(PITCH, getRandom().nextFloat() * 3.0F);
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
	public SignBlockEntity getSignTileEntity() {
		return signTileEntity;
	}

	@Override
	public Component getSignText(int index) {
		return entityData.get(SIGN_TEXT.get(index));
	}

	@Override
	public List<Component> getSignTextList() {
		return IntStream.range(0, SIGN_TEXT.size()).mapToObj(this::getSignText).toList();
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
		this.goalSelector.addGoal(2, new EntityAISearchAndEatMelons(this));
		this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D, 1.0000001E-5F));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, (e,_) -> e instanceof Enemy));
	}

	@Override
	public void performRangedAttack(LivingEntity target, float distanceFactor) {
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
	public boolean isShearable(@org.jetbrains.annotations.Nullable Player player, ItemStack item, Level level, BlockPos pos) {
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
			PacketDistributor.sendToPlayersTrackingEntity(this, new ClientPacketMelonAmbientSound(this));
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 vec) {
		if (config.getHats().map(ModConfigSpec.BooleanValue::isFalse).orElse(true)
			|| player.getMainHandItem().getItem() instanceof ShearsItem
			|| player.getOffhandItem().getItem() instanceof ShearsItem
		)
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
		List<ItemStack> list = Lists.newArrayList(config.getShear().map(ModConfigSpec.BooleanValue::isTrue).orElse(false) ? getHead() : ItemStack.EMPTY);
		setHead(ItemStack.EMPTY);
		return list;
	}

	@Override
	public void die(DamageSource cause) {
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

	@Override
	public void addAdditionalSaveData(ValueOutput compound) {
		super.addAdditionalSaveData(compound);
		compound.storeNullable("head", ItemStack.CODEC, getHead());
		compound.putBoolean("glowingText", glowingText());
		compound.putInt("textColor", getTextColor().getId());
		for (int i = 0; i < 4; i++) {
			compound.storeNullable("Text" + (i + 1), ComponentSerialization.CODEC, getSignText(i));
		}
	}

	@Override
	public void readAdditionalSaveData(ValueInput compound) {
		super.readAdditionalSaveData(compound);
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

	public float getPitch() {
		return entityData.get(PITCH);
	}

	class EntityAISearchAndEatMelons extends Goal {

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
			melonblock = entity instanceof EntityGlisteringMelonGolem ? MOD_BLOCKS.get().GLISTERING_MELON.get() : Blocks.MELON;
		}

		@Override
		public boolean canUse() {
			return config.getEats().map(ModConfigSpec.BooleanValue::isTrue).orElse(false) && parent.getHealth() < parent.getMaxHealth();
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
					parent.heal(config.getHeal().map(v -> v.get().floatValue()).orElse(1F) * (flag ? 9 : 1));
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
									ResourceHandler<ItemResource> cap = parent.level().getCapability(Capabilities.Item.BLOCK, mutableBlockPos, Direction.UP);
									if (cap != null) {
										for (int i = 0; i < cap.size(); i++) {
											if (isMelon(cap.getResource(i).getItem())) {
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
				if (te == null || parent.level().getCapability(Capabilities.Item.BLOCK, mutableBlockPos, Direction.UP) == null) {
					parent.getNavigation().stop();
					foundMelon = false;
					return;
				}
				ResourceHandler<ItemResource> handler = parent.level().getCapability(Capabilities.Item.BLOCK, mutableBlockPos, Direction.UP);
				if (handler != null) {
					boolean valid = false;
					int i;
					for (i = 0; i < handler.size(); i++) {
						if (isMelon(handler.getResource(i).getItem())) {
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
						boolean flag = handler.getResource(i).getItem() == melonblock.asItem();
						try (Transaction transaction = Transaction.openRoot()) {
							handler.extract(i, handler.getResource(i), 1, transaction);
							parent.playSound(SoundEvents.PLAYER_BURP, 1F, 1F);
							parent.heal(config.getHeal().map(v -> v.get().floatValue()).orElse(1F) * (flag ? 9 : 1));
							cooldown = 10 + parent.getRandom().nextInt(40);
							transaction.commit();
						}
					}
				} else {
					parent.getNavigation().stop();
					foundMelon = false;
				}
			}
		}
	}
}

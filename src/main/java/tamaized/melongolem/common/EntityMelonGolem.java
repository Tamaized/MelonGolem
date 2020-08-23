package tamaized.melongolem.common;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandException;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.network.client.ClientPacketHandlerMelonAmbientSound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class EntityMelonGolem extends GolemEntity implements IRangedAttackMob, IForgeShearable, IEntityAdditionalSpawnData, IModProxy.ISignHolder {

	private static final DataParameter<ItemStack> HEAD = EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.ITEMSTACK);
	//	private static final ResourceLocation LOOT = LootTables.register(new ResourceLocation(MelonMod.MODID, "melongolem"));
	private static final List<DataParameter<ITextComponent>> SIGN_TEXT = Lists.newArrayList(

			EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.TEXT_COMPONENT),

			EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.TEXT_COMPONENT),

			EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.TEXT_COMPONENT),

			EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.TEXT_COMPONENT)

	);
	public static BlockState SIGN_TILE_BLOCKSTATE = Blocks.OAK_WALL_SIGN.getDefaultState();
	public static final SignTileEntity te = new SignTileEntity() {
		@Nonnull
		@Override
		public BlockState getBlockState() {
			return SIGN_TILE_BLOCKSTATE;
		}
	};
	private static final DataParameter<Float> PITCH = EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.FLOAT);

	public EntityMelonGolem(World worldIn) {
		this(Objects.requireNonNull(MelonMod.entityTypeMelonGolem), worldIn);
	}

	protected EntityMelonGolem(EntityType<? extends GolemEntity> p_i48569_1_, World p_i48569_2_) {
		super(p_i48569_1_, p_i48569_2_);
	}

	public static AttributeModifierMap.MutableAttribute registerAttributes() {
		return MobEntity.func_233666_p_().
				createMutableAttribute(Attributes.MAX_HEALTH, MelonMod.config.health.get().floatValue()).
				createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2F);
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(HEAD, ItemStack.EMPTY);
		for (DataParameter<ITextComponent> sign : SIGN_TEXT)
			dataManager.register(sign, new StringTextComponent(""));
		dataManager.register(PITCH, rand.nextFloat() * 3.0F);
	}

	@Override
	public void setSignText(int index, ITextComponent text) {
		dataManager.set(SIGN_TEXT.get(index), text);
	}

	@Override
	public int networkID() {
		return getEntityId();
	}

	@Override
	public ITextComponent getSignText(int index) {
		return dataManager.get(SIGN_TEXT.get(index));
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
		this.goalSelector.addGoal(2, new EntityAISearchAndEatMelons(this));
		this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 1.0D, 1.0000001E-5F));
		this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(5, new LookRandomlyGoal(this));

		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, MobEntity.class, 10, true, false, (e) -> e instanceof IMob));
	}

	@Override
	public void attackEntityWithRangedAttack(@Nonnull LivingEntity target, float distanceFactor) {
		EntityMelonSlice slice = new EntityMelonSlice(this.world, this);
		double d0 = target.getPosY() + (double) target.getEyeHeight() - 1.100000023841858D;
		double d1 = target.getPosX() - this.getPosX();
		double d2 = d0 - slice.getPosY();
		double d3 = target.getPosZ() - this.getPosZ();
		float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
		slice.shoot(d1, d2 + (double) f, d3, 1.6F, 12.0F);
		this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		slice.setPositionAndUpdate(slice.getPosX(), slice.getPosY(), slice.getPosZ());
		MelonMod.spawnNonLivingEntity(world, slice);
	}

	@Override
	public boolean isShearable(@Nonnull ItemStack item, World world, BlockPos pos) {
		return !getHead().isEmpty();
	}

	/*@Override
	@Nullable
	protected ResourceLocation getLootTable() {
		return LOOT;
	}*/

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		return SoundEvents.ENTITY_SLIME_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SLIME_DEATH;
	}

	@Override
	public int getTalkInterval() {
		return MelonMod.SIGNS.contains(getHead().getItem()) ? 200 : super.getTalkInterval();
	}

	@Override
	public float getSoundPitch() { // protected -> public
		return super.getSoundPitch();
	}

	@Override
	public float getSoundVolume() { // protected -> public
		return super.getSoundVolume();
	}

	@Override
	public void playAmbientSound() {
		if (world != null && !world.isRemote)
			MelonMod.network.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new ClientPacketHandlerMelonAmbientSound(this));
	}

	@Nonnull
	@Override
	public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand) {
		if (!MelonMod.config.hats.get() || player.getHeldItemMainhand().getItem() instanceof ShearsItem || player.getHeldItemOffhand().getItem() instanceof ShearsItem)
			return ActionResultType.FAIL;
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty() && getHead().isEmpty()) {
			if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR || MelonMod.SIGNS.contains(stack.getItem())) {
				setHead(stack);
				if (!player.isCreative())
					player.getHeldItem(hand).shrink(1);
				return ActionResultType.SUCCESS;
			}
		} else if (!getHead().isEmpty() && MelonMod.SIGNS.contains(getHead().getItem())) {
			MelonMod.proxy.openSignHolderGui(this);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}

	@Override
	protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
		return 1.7F;
	}

	@Override
	public ItemStack getHead() {
		return dataManager.get(HEAD);
	}

	public void setHead(ItemStack stack) {
		for (int i = 0; i < 4; ++i)
			setSignText(i, new StringTextComponent(""));
		ItemStack newstack = stack.copy();
		newstack.setCount(1);
		dataManager.set(HEAD, newstack);
	}

	@Nonnull
	@Override
	public List<ItemStack> onSheared(@Nullable PlayerEntity player, @Nonnull ItemStack item, World world, BlockPos pos, int fortune) {
		List<ItemStack> list = Lists.newArrayList(MelonMod.config.shear.get() ? getHead() : ItemStack.EMPTY);
		setHead(ItemStack.EMPTY);
		return list;
	}

	@Override
	public void onDeath(@Nonnull DamageSource cause) {
		super.onDeath(cause);
		ItemStack stack = getHead();
		if (!world.isRemote && !stack.isEmpty()) {
			ItemEntity e = new ItemEntity(world, getPosX(), getPosY(), getPosZ(), stack);
			e.setMotion(e.getMotion().add(

					rand.nextFloat() * 0.05F,

					(rand.nextFloat() - rand.nextFloat()) * 0.1F,

					(rand.nextFloat() - rand.nextFloat()) * 0.1F

			));
			world.addEntity(e);
		}
	}

	@Nonnull
	@Override
	public CompoundNBT writeWithoutTypeId(CompoundNBT compound) {
		compound.put("head", getHead().serializeNBT());
		for (int i = 0; i < 4; ++i) {
			String s = ITextComponent.Serializer.toJson(getSignText(i));
			compound.putString("Text" + (i + 1), s);
		}
		return super.writeWithoutTypeId(compound);
	}

	@Override
	public void read(CompoundNBT compound) {
		setHead(ItemStack.read(compound.getCompound("head")));
		for (int i = 0; i < 4; ++i) {
			String s = compound.getString("Text" + (i + 1));
			ITextComponent itextcomponent = ITextComponent.Serializer.func_240643_a_(s);

			try {
				setSignText(i, itextcomponent == null ? new StringTextComponent("") : TextComponentUtils.func_240645_a_(getCommandSource(), itextcomponent, null, 0));
			} catch (CommandException | CommandSyntaxException var7) {
				setSignText(i, itextcomponent);
			}
		}
		super.read(compound);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeItemStack(getHead());
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		setHead(additionalData.readItemStack());
	}

	public float getPitch() {
		return dataManager.get(PITCH);
	}

	static class EntityAISearchAndEatMelons extends Goal {

		private final MobEntity parent;
		private final Item melon;
		private final Block melonblock;
		private int cooldown;
		private BlockPos.Mutable pos = new BlockPos.Mutable();
		private boolean foundMelon = false;

		EntityAISearchAndEatMelons(MobEntity entity) {
			parent = entity;
			setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
			melon = entity instanceof EntityGlisteringMelonGolem ? Items.GLISTERING_MELON_SLICE : Items.MELON_SLICE;
			melonblock = entity instanceof EntityGlisteringMelonGolem ? MelonMod.glisteringMelonBlock : Blocks.MELON;
		}

		@Override
		public boolean shouldExecute() {
			return MelonMod.config.eats.get() && parent.getHealth() < parent.getMaxHealth();
		}

		@Override
		public void resetTask() {
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
			AxisAlignedBB area = new AxisAlignedBB(parent.getPosX() - radius, parent.getPosY() - radius, parent.getPosZ() - radius, parent.getPosX() + radius, parent.getPosY() + radius, parent.getPosZ() + radius);
			List<ItemEntity> items = parent.world.getEntitiesWithinAABB(ItemEntity.class, area);
			for (ItemEntity item : items) {
				if (parent.getNavigator().noPath() && isMelon(item)) {
					parent.getNavigator().tryMoveToEntityLiving(item, 1.25F);
					parent.getLookController().setLookPositionWithEntity(item, 30.0F, 30.0F);
				}
				if (cooldown <= 0 && item.isAlive() && isMelon(item) && item.getBoundingBox().intersects(parent.getBoundingBox().grow(1))) {
					boolean flag = item.getItem().getItem() == melonblock.asItem();
					item.getItem().shrink(1);
					parent.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1F, 1F);
					parent.heal(MelonMod.config.heal.get().floatValue() * (flag ? 9 : 1));
					cooldown = 30 + parent.getRNG().nextInt(40);
				}
			}
			if (parent.getNavigator().noPath()) {
				foundMelon = false;
				search:
				for (int x = -radius; x < radius; x++)
					for (int y = -radius; y < radius; y++)
						for (int z = -radius; z < radius; z++) {
							pos.setPos(parent.getPosition().add(x, y, z));
							if (parent.world.isBlockLoaded(pos)) {
								TileEntity te = parent.world.getTileEntity(pos);
								if (te != null) {
									te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).ifPresent(cap -> {
										for (int i = 0; i < cap.getSlots(); i++) {
											if (isMelon(cap.getStackInSlot(i))) {
												foundMelon = parent.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) < 4 || parent.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.25F);
												break;
											}
										}
									});
									if (foundMelon)
										break search;
								}
							}
						}
			}
			if (foundMelon) {
				// Validate
				if (!parent.world.isBlockLoaded(pos)) {
					parent.getNavigator().clearPath();
					foundMelon = false;
					return;
				}
				TileEntity te = parent.world.getTileEntity(pos);
				if (te == null || !te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).isPresent()) {
					parent.getNavigator().clearPath();
					foundMelon = false;
					return;
				}
				LazyOptional<IItemHandler> handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
				if (handler.isPresent()) {
					handler.ifPresent(cap -> {
						boolean valid = false;
						int i;
						for (i = 0; i < cap.getSlots(); i++) {
							if (isMelon(cap.getStackInSlot(i))) {
								valid = true;
								break;
							}
						}
						if (!valid) {
							parent.getNavigator().clearPath();
							foundMelon = false;
							return;
						}

						if (cooldown <= 0 && parent.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) < 4) {
							boolean flag = cap.getStackInSlot(i).getItem() == melonblock.asItem();
							cap.getStackInSlot(i).shrink(1);
							parent.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1F, 1F);
							parent.heal(MelonMod.config.heal.get().floatValue() * (flag ? 9 : 1));
							cooldown = 10 + parent.getRNG().nextInt(40);
						}
					});
				} else {
					parent.getNavigator().clearPath();
					foundMelon = false;
				}
			}
		}
	}
}

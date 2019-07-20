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
import net.minecraft.entity.SharedMonsterAttributes;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.MelonSounds;
import tamaized.melongolem.common.capability.CapabilityList;
import tamaized.melongolem.network.client.ClientPacketHandlerMelonTTS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class EntityMelonGolem extends GolemEntity implements IRangedAttackMob, IShearable, IEntityAdditionalSpawnData, IModProxy.ISignHolder {

	public static final SignTileEntity te = new SignTileEntity() {
		@Nonnull
		@Override
		public BlockState getBlockState() {
			return Blocks.OAK_WALL_SIGN.getDefaultState();
		}
	};
	private static final DataParameter<ItemStack> HEAD = EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.ITEMSTACK);
	//	private static final ResourceLocation LOOT = LootTables.register(new ResourceLocation(MelonMod.MODID, "melongolem"));
	private static final List<DataParameter<ITextComponent>> SIGN_TEXT = Lists.newArrayList(

			EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.TEXT_COMPONENT),

			EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.TEXT_COMPONENT),

			EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.TEXT_COMPONENT),

			EntityDataManager.createKey(EntityMelonGolem.class, DataSerializers.TEXT_COMPONENT)

	);
	private final float pitch = rand.nextFloat() * 3.0F;

	public EntityMelonGolem(World worldIn) {
		this(Objects.requireNonNull(MelonMod.entityTypeMelonGolem), worldIn);
	}

	protected EntityMelonGolem(EntityType<? extends GolemEntity> p_i48569_1_, World p_i48569_2_) {
		super(p_i48569_1_, p_i48569_2_);
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(HEAD, ItemStack.EMPTY);
		for (DataParameter<ITextComponent> sign : SIGN_TEXT)
			dataManager.register(sign, new StringTextComponent(""));
	}

	@Override
	public void setSignText(int index, ITextComponent text) {
		dataManager.set(SIGN_TEXT.get(index), text);
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
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MelonMod.config.health.get());
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
	}

	@Override
	public void attackEntityWithRangedAttack(@Nonnull LivingEntity target, float distanceFactor) {
		EntityMelonSlice slice = new EntityMelonSlice(this.world, this);
		double d0 = target.posY + (double) target.getEyeHeight() - 1.100000023841858D;
		double d1 = target.posX - this.posX;
		double d2 = d0 - slice.posY;
		double d3 = target.posZ - this.posZ;
		float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;
		slice.shoot(d1, d2 + (double) f, d3, 1.6F, 12.0F);
		this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		slice.setPositionAndUpdate(slice.posX, slice.posY, slice.posZ);
		this.world.addEntity(slice);
	}

	@Override
	public boolean isShearable(@Nonnull ItemStack item, IWorldReader world, BlockPos pos) {
		return !getHead().isEmpty();
	}

	/*@Override
	@Nullable
	protected ResourceLocation getLootTable() {
		return LOOT;
	}*/

	@Override
	protected float getSoundPitch() {
		return MelonMod.config.tehnutMode.get() ? pitch + rand.nextFloat() * 0.25F - 0.50F : super.getSoundPitch();
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return MelonMod.config.tehnutMode.get() ? MelonSounds.daddy : SoundEvents.ENTITY_SLIME_SQUISH_SMALL;
	}

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
		return getHead().getItem() == Items.OAK_SIGN ? 200 : super.getTalkInterval();
	}

	@Override
	public void playAmbientSound() {
		if (MelonMod.config.tts.get() && getHead().getItem() == Items.OAK_SIGN) {
			if (world != null && !world.isRemote)
				MelonMod.network.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new ClientPacketHandlerMelonTTS(this));
		} else
			super.playAmbientSound();
	}

	@Override
	@SuppressWarnings("deprecation")
	protected boolean processInteract(PlayerEntity player, Hand hand) {
		if (!MelonMod.config.hats.get() || player.getHeldItemMainhand().getItem() instanceof ShearsItem || player.getHeldItemOffhand().getItem() instanceof ShearsItem)
			return false;
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty() && getHead().isEmpty()) {
			if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR || stack.getItem() == Items.OAK_SIGN) {
				setHead(stack);
				if (!player.isCreative())
					player.getHeldItem(hand).shrink(1);
				return true;
			}
		} else if (!getHead().isEmpty() && getHead().getItem() == Items.OAK_SIGN) {
			MelonMod.proxy.openSignHolderGui(this);
			return true;
		}
		return false;
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
	public List<ItemStack> onSheared(@Nonnull ItemStack item, IWorld world, BlockPos pos, int fortune) {
		List<ItemStack> list = Lists.newArrayList(MelonMod.config.shear.get() ? getHead() : ItemStack.EMPTY);
		setHead(ItemStack.EMPTY);
		return list;
	}

	@Override
	public void onDeath(@Nonnull DamageSource cause) {
		super.onDeath(cause);
		ItemStack stack = getHead();
		if (!world.isRemote && !stack.isEmpty()) {
			ItemEntity e = new ItemEntity(world, posX, posY, posZ, stack);
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
	@SuppressWarnings("deprecation")
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
			ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(s);

			try {
				setSignText(i, itextcomponent == null ? new StringTextComponent("") : TextComponentUtils.updateForEntity(getCommandSource(), itextcomponent, null, 0));
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

	static class EntityAISearchAndEatMelons extends Goal {

		private final MobEntity parent;
		private final Item melon;
		private final Block melonblock;
		private int cooldown;
		private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
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
			AxisAlignedBB area = new AxisAlignedBB(parent.posX - radius, parent.posY - radius, parent.posZ - radius, parent.posX + radius, parent.posY + radius, parent.posZ + radius);
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
									IItemHandler cap = CapabilityList.getCap(te, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
									if (cap != null)
										for (int i = 0; i < cap.getSlots(); i++) {
											if (isMelon(cap.getStackInSlot(i))) {
												foundMelon = parent.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) < 4 || parent.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.25F);
												if (foundMelon)
													break search;
											}
										}
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
				IItemHandler cap = CapabilityList.getCap(te, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
				if (cap == null) {
					parent.getNavigator().clearPath();
					foundMelon = false;
					return;
				}
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
			}
		}
	}
}

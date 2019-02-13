package tamaized.melongolem.common;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.MelonConfig;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.capability.CapabilityList;
import tamaized.melongolem.common.capability.ITinyGolemCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EntityTinyMelonGolem extends EntityTameable implements IShearable, IEntityAdditionalSpawnData, IModProxy.ISignHolder {

	public static final TileEntitySign te = new TileEntitySign() {
		@Nonnull
		@Override
		public Block getBlockType() {
			return Blocks.WALL_SIGN;
		}

		@Override
		public int getBlockMetadata() {
			return 0;
		}
	};
	private static final DataParameter<ItemStack> HEAD = EntityDataManager.createKey(EntityTinyMelonGolem.class, DataSerializers.ITEM_STACK);
	private static final List<DataParameter<ITextComponent>> SIGN_TEXT = Lists.newArrayList(

			EntityDataManager.createKey(EntityTinyMelonGolem.class, DataSerializers.TEXT_COMPONENT),

			EntityDataManager.createKey(EntityTinyMelonGolem.class, DataSerializers.TEXT_COMPONENT),

			EntityDataManager.createKey(EntityTinyMelonGolem.class, DataSerializers.TEXT_COMPONENT),

			EntityDataManager.createKey(EntityTinyMelonGolem.class, DataSerializers.TEXT_COMPONENT)

	);

	public EntityTinyMelonGolem(World worldIn) {
		super(worldIn);
		this.setSize(0.175F, 0.475F);
	}

	@Nullable
	@Override
	public EntityAgeable createChild(@Nonnull EntityAgeable ageable) {
		return null;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(HEAD, ItemStack.EMPTY);
		for (DataParameter<ITextComponent> sign : SIGN_TEXT)
			dataManager.register(sign, new TextComponentString(""));
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (world.isRemote || !isEntityAlive())
			return;
		ITinyGolemCapability cap = CapabilityList.getCap(getOwner(), CapabilityList.TINY_GOLEM, null);
		if (cap != null && getOwner() instanceof EntityPlayer) {
			if (cap.getPet() != this) {
				if (cap.getPet() != null && cap.getPet().getPersistentID().equals(this.getPersistentID())) {
					setDead();
					return;
				}
				ItemMelonStick.summonPet(world, (EntityPlayer) getOwner(), this);
				if (cap.getPet() == null)
					cap.setPet(this);
				else
					this.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
			}
		}
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
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAIFollowOwner(this, 1.0D, 4.0F, 2.0F));
		tasks.addTask(1, new EntityAIWanderAvoidWater(this, 1.0D));
		tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(2, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MelonConfig.health);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
	}

	@Override
	public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos) {
		return !getHead().isEmpty();
	}

	@Override
	protected float getSoundPitch() {
		return rand.nextFloat() * 0.5F + 2F;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SMALL_SLIME_SQUISH;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SLIME_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SLIME_DEATH;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (player.getHeldItemMainhand().getItem() instanceof ItemShears || player.getHeldItemOffhand().getItem() instanceof ItemShears)
			return false;
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty() && getHead().isEmpty()) {
			if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR || stack.getItem() == Items.SIGN) {
				setHead(stack);
				if (!player.isCreative())
					player.getHeldItem(hand).shrink(1);
				return true;
			}
		} else if (!getHead().isEmpty() && getHead().getItem() == Items.SIGN) {
			MelonMod.proxy.openSignHolderGui(this);
			return true;
		}
		return false;
	}

	@Override
	public float getEyeHeight() {
		return 0.425F;
	}

	@Override
	public ItemStack getHead() {
		return dataManager.get(HEAD);
	}

	public void setHead(ItemStack stack) {
		for (int i = 0; i < 4; ++i)
			setSignText(i, new TextComponentString(""));
		ItemStack newstack = stack.copy();
		newstack.setCount(1);
		dataManager.set(HEAD, newstack);
	}

	@Nonnull
	@Override
	public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		List<ItemStack> list = Lists.newArrayList(MelonConfig.shear ? getHead() : ItemStack.EMPTY);
		setHead(ItemStack.EMPTY);
		return list;
	}

	@Override
	public void onDeath(@Nonnull DamageSource cause) {
		super.onDeath(cause);
		ItemStack stack = getHead();
		if (!world.isRemote && !stack.isEmpty()) {
			EntityItem e = new EntityItem(world, posX, posY, posZ, stack);
			e.motionY += rand.nextFloat() * 0.05F;
			e.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
			e.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
			world.spawnEntity(e);
		}
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		Block block = Block.getBlockFromItem(getHead().getItem());
		compound.setInteger("state", getHead().getItem() == Items.SIGN ? -100 : Block.getStateId(block.getStateFromMeta(getHead().getMetadata())));
		for (int i = 0; i < 4; ++i) {
			String s = ITextComponent.Serializer.componentToJson(getSignText(i));
			compound.setString("Text" + (i + 1), s);
		}
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		int stateid = compound.getInteger("state");
		if (stateid != -100) {
			IBlockState state = Block.getStateById(stateid);
			setHead(state.getBlock().getPickBlock(state, null, world, BlockPos.ORIGIN, null));
		} else
			setHead(new ItemStack(Items.SIGN));

		ICommandSender icommandsender = new ICommandSender() {
			@Override
			public String getName() {
				return "Sign";
			}

			@Override
			public boolean canUseCommand(int permLevel, @Nonnull String commandName) {
				return permLevel <= 2;
			}

			@Override
			public BlockPos getPosition() {
				return te.getPos();
			}

			@Override
			public Vec3d getPositionVector() {
				return new Vec3d((double) te.getPos().getX() + 0.5D, te.getPos().getY() + 0.5D, (double) te.getPos().getZ() + 0.5D);
			}

			@Nonnull
			@Override
			public World getEntityWorld() {
				return world;
			}

			@Override
			public MinecraftServer getServer() {
				return world.getMinecraftServer();
			}
		};
		for (int i = 0; i < 4; ++i) {
			String s = compound.getString("Text" + (i + 1));
			ITextComponent itextcomponent = ITextComponent.Serializer.jsonToComponent(s);

			try {
				//noinspection ConstantConditions
				setSignText(i, itextcomponent == null ? new TextComponentString("") : TextComponentUtils.processComponent(icommandsender, itextcomponent, null));
			} catch (CommandException var7) {
				setSignText(i, itextcomponent);
			}
		}
		super.readFromNBT(compound);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeItemStack(buffer, getHead());
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		setHead(ByteBufUtils.readItemStack(additionalData));
	}

}

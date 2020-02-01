package tamaized.melongolem.common;

import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamaized.melongolem.MelonMod;

import javax.annotation.Nonnull;
import java.util.Objects;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class EntityMelonSlice extends ThrowableEntity implements IRendersAsItem {

	private static final DataParameter<Boolean> GLIST = EntityDataManager.createKey(EntityMelonSlice.class, DataSerializers.BOOLEAN);
	private static ItemStack cacheRenderStack = ItemStack.EMPTY;

	@SuppressWarnings("unused")
	public EntityMelonSlice(World worldIn) {
		super(Objects.requireNonNull(MelonMod.entityTypeMelonSlice), worldIn);
	}

	public EntityMelonSlice(World worldIn, LivingEntity throwerIn) {
		super(Objects.requireNonNull(MelonMod.entityTypeMelonSlice), throwerIn, worldIn);
		if (throwerIn instanceof EntityGlisteringMelonGolem)
			setGlist();
	}

	@SuppressWarnings("unused")
	public EntityMelonSlice(World worldIn, double x, double y, double z) {
		super(Objects.requireNonNull(MelonMod.entityTypeMelonSlice), x, y, z, worldIn);
	}

	@Override
	protected void registerData() {
		dataManager.register(GLIST, false);
	}

	public boolean isGlistering() {
		return dataManager.get(GLIST);
	}

	public EntityMelonSlice setGlist() {
		dataManager.set(GLIST, true);
		return this;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 3) {
			for (int i = 0; i < 8; ++i) {
				this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, rand.nextInt() == 0 ? new ItemStack(Items.MELON_SEEDS) : new ItemStack(Items.MELON_SLICE)), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {
		if (result instanceof EntityRayTraceResult) {
			if (((EntityRayTraceResult) result).getEntity() == getThrower())
				return;
			((EntityRayTraceResult) result).getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), MelonMod.config.damage.get().floatValue() * (isGlistering() ? MelonMod.config.glisterDamageAmp.get().floatValue() : 1F));
		}

		if (!this.world.isRemote) {
			this.world.setEntityState(this, (byte) 3);
			this.remove();
		}
	}

	@Nonnull
	@Override
	public ItemStack getItem() {
		return cacheRenderStack.isEmpty() ? cacheRenderStack = new ItemStack(isGlistering() ? Items.GLISTERING_MELON_SLICE : Items.MELON_SLICE) : cacheRenderStack;
	}
}

package tamaized.melongolem.common;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamaized.melongolem.MelonMod;

import javax.annotation.Nonnull;
import java.util.Objects;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class EntityMelonSlice extends ThrowableProjectile implements ItemSupplier {

	private static final EntityDataAccessor<Boolean> GLIST = SynchedEntityData.defineId(EntityMelonSlice.class, EntityDataSerializers.BOOLEAN);
	private static ItemStack cacheRenderStack = ItemStack.EMPTY;

	@SuppressWarnings("unused")
	public EntityMelonSlice(Level worldIn) {
		super(Objects.requireNonNull(MelonMod.ENTITY_TYPE_MELON_SLICE.get()), worldIn);
	}

	public EntityMelonSlice(Level worldIn, LivingEntity throwerIn) {
		super(Objects.requireNonNull(MelonMod.ENTITY_TYPE_MELON_SLICE.get()), throwerIn, worldIn);
		if (throwerIn instanceof EntityGlisteringMelonGolem)
			setGlist();
	}

	@SuppressWarnings("unused")
	public EntityMelonSlice(Level worldIn, double x, double y, double z) {
		super(Objects.requireNonNull(MelonMod.ENTITY_TYPE_MELON_SLICE.get()), x, y, z, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(GLIST, false);
	}

	public boolean isGlistering() {
		return entityData.get(GLIST);
	}

	public EntityMelonSlice setGlist() {
		entityData.set(GLIST, true);
		return this;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleEntityEvent(byte id) {
		if (id == 3) {
			for (int i = 0; i < 8; ++i) {
				this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, random.nextInt() == 0 ? new ItemStack(Items.MELON_SEEDS) : new ItemStack(Items.MELON_SLICE)), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void onHit(@Nonnull HitResult result) {
		super.onHit(result);
		if (!this.level.isClientSide) {
			this.level.broadcastEntityEvent(this, (byte) 3);
			this.discard();
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (result.getEntity() == getOwner())
			return;
		result.getEntity().hurt(DamageSource.thrown(this, getOwner()), MelonMod.config.damage.get().floatValue() * (isGlistering() ? MelonMod.config.glisterDamageAmp.get().floatValue() : 1F));

	}

	@Nonnull
	@Override
	public ItemStack getItem() {
		return cacheRenderStack.isEmpty() ? cacheRenderStack = new ItemStack(isGlistering() ? Items.GLISTERING_MELON_SLICE : Items.MELON_SLICE) : cacheRenderStack;
	}
}

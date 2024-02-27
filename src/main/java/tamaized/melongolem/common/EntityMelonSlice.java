package tamaized.melongolem.common;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.registry.ModEntities;

import javax.annotation.Nonnull;
import java.util.Objects;

public class EntityMelonSlice extends ThrowableProjectile implements ItemSupplier {

	private static final EntityDataAccessor<Boolean> GLIST = SynchedEntityData.defineId(EntityMelonSlice.class, EntityDataSerializers.BOOLEAN);
	private static ItemStack cacheRenderStack = ItemStack.EMPTY;

	public EntityMelonSlice(Level level) {
		this(ModEntities.MELON_SLICE.get(), level);
	}

	public EntityMelonSlice(EntityType<? extends EntityMelonSlice> type, Level level) {
		super(type, level);
	}

	public EntityMelonSlice(Level level, LivingEntity thrower) {
		super(ModEntities.MELON_SLICE.get(), thrower, level);
		if (thrower instanceof EntityGlisteringMelonGolem)
			setGlist();
	}

	public EntityMelonSlice(Level level, double x, double y, double z) {
		super(ModEntities.MELON_SLICE.get(), x, y, z, level);
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
	public void handleEntityEvent(byte id) {
		if (id == 3) {
			for (int i = 0; i < 8; ++i) {
				this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, random.nextInt() == 0 ? new ItemStack(Items.MELON_SEEDS) : new ItemStack(Items.MELON_SLICE)), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void onHit(@Nonnull HitResult result) {
		super.onHit(result);
		if (!this.level().isClientSide()) {
			this.level().broadcastEntityEvent(this, (byte) 3);
			this.discard();
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (result.getEntity() == getOwner())
			return;
		result.getEntity().hurt(this.damageSources().thrown(this, getOwner()), MelonMod.config.damage.get().floatValue() * (isGlistering() ? MelonMod.config.glisterDamageAmp.get().floatValue() : 1F));

	}

	@Nonnull
	@Override
	public ItemStack getItem() {
		return cacheRenderStack.isEmpty() ? cacheRenderStack = new ItemStack(isGlistering() ? Items.GLISTERING_MELON_SLICE : Items.MELON_SLICE) : cacheRenderStack;
	}
}

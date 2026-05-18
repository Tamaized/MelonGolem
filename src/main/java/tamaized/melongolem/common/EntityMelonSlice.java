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
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.Lazy;
import tamaized.beanification.Autowired;
import tamaized.beanification.BeanContext;
import tamaized.beanification.Configurable;
import tamaized.melongolem.config.common.CommonConfig;
import tamaized.melongolem.registry.ModEntities;

@Configurable
public class EntityMelonSlice extends ThrowableProjectile implements ItemSupplier {

	private static final Lazy<ModEntities> MOD_ENTITIES = BeanContext.injectLazy(ModEntities.class);

	private static final EntityDataAccessor<Boolean> GLIST = SynchedEntityData.defineId(EntityMelonSlice.class, EntityDataSerializers.BOOLEAN);

	@Autowired
	private CommonConfig config;

	private ItemStack cacheRenderStack = ItemStack.EMPTY;

	public EntityMelonSlice(EntityType<? extends EntityMelonSlice> type, Level level) {
		super(type, level);
	}

	public EntityMelonSlice(Level level, LivingEntity thrower) {
		super(MOD_ENTITIES.get().MELON_SLICE.get(), thrower.getX(), thrower.getEyeY(), thrower.getZ(), level);
		if (thrower instanceof EntityGlisteringMelonGolem)
			markGlistering();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(GLIST, false);
	}

	public boolean isGlistering() {
		return entityData.get(GLIST);
	}

	public void markGlistering() {
		entityData.set(GLIST, true);
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == 3) {
			for (int i = 0; i < 8; ++i) {
				this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, random.nextInt() == 0 ? Items.MELON_SEEDS : Items.MELON_SLICE), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (!this.level().isClientSide()) {
			this.level().broadcastEntityEvent(this, (byte) 3);
			this.discard();
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (result.getEntity() == getOwner() || config.getDamage().isEmpty())
			return;

		final float glisterAmp = config.getGlisterDamageAmp()
			.filter(_ -> isGlistering())
			.map(ModConfigSpec.ConfigValue::get)
			.map(Double::floatValue)
			.orElse(1F);

		result.getEntity().hurt(
			this.damageSources().thrown(this, getOwner()),
			config.getDamage().get().get().floatValue() * glisterAmp
		);

	}

	@Override
	public ItemStack getItem() {
		return cacheRenderStack.isEmpty() ? cacheRenderStack = new ItemStack(isGlistering() ? Items.GLISTERING_MELON_SLICE : Items.MELON_SLICE) : cacheRenderStack;
	}
}

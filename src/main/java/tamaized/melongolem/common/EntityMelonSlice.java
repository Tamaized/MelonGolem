package tamaized.melongolem.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamaized.melongolem.MelonMod;

import javax.annotation.Nonnull;
import java.util.Objects;

public class EntityMelonSlice extends EntityThrowable {

	private static final DataParameter<Boolean> GLIST = EntityDataManager.createKey(EntityMelonSlice.class, DataSerializers.BOOLEAN);

	@SuppressWarnings("unused")
	public EntityMelonSlice(World worldIn) {
		super(Objects.requireNonNull(MelonMod.entityTypeMelonSlice), worldIn);
	}

	public EntityMelonSlice(World worldIn, EntityLivingBase throwerIn) {
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
		super.registerData();
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
				this.world.spawnParticle(new ItemParticleData(Particles.ITEM, rand.nextInt() == 0 ? new ItemStack(Items.MELON_SEEDS) : new ItemStack(Items.MELON_SLICE)), this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {
		if (result.entity != null) {
			if (result.entity == getThrower())
				return;
			result.entity.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), MelonMod.config.damage.get().floatValue() * (isGlistering() ? MelonMod.config.glisterDamageAmp.get().floatValue() : 1F));
		}

		if (!this.world.isRemote) {
			this.world.setEntityState(this, (byte) 3);
			this.remove();
		}
	}
}

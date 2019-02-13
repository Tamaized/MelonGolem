package tamaized.melongolem.common.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tamaized.melongolem.MelonMod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = MelonMod.MODID)
public class CapabilityList {

	@CapabilityInject(ITinyGolemCapability.class)
	public static final Capability<ITinyGolemCapability> TINY_GOLEM;

	static {
		TINY_GOLEM = null;
	}

	@SubscribeEvent
	public static void attachCapabilityEntity(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof EntityPlayer) {
			e.addCapability(ITinyGolemCapability.ID, new ICapabilitySerializable<NBTTagCompound>() {

				ITinyGolemCapability inst = TINY_GOLEM.getDefaultInstance();

				@Override
				public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
					return capability == TINY_GOLEM;
				}

				@Override
				public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
					return capability == TINY_GOLEM ? TINY_GOLEM.<T>cast(inst) : null;
				}

				@Override
				public NBTTagCompound serializeNBT() {
					return (NBTTagCompound) TINY_GOLEM.getStorage().writeNBT(TINY_GOLEM, inst, null);
				}

				@Override
				public void deserializeNBT(NBTTagCompound nbt) {
					TINY_GOLEM.getStorage().readNBT(TINY_GOLEM, inst, null, nbt);
				}

			});
		}
	}

	@SubscribeEvent
	public static void updateClone(PlayerEvent.Clone e) {
		ITinyGolemCapability newcap = getCap(e.getEntityPlayer(), TINY_GOLEM, null);
		ITinyGolemCapability oldcap = getCap(e.getOriginal(), TINY_GOLEM, null);
		if (newcap != null && oldcap != null)
			newcap.copyFrom(oldcap);
	}

	public static <T> T getCap(@Nullable ICapabilityProvider provider, Capability<T> cap, @Nullable EnumFacing face) {
		return provider != null && provider.hasCapability(cap, face) ? provider.getCapability(cap, face) : null;
	}

}

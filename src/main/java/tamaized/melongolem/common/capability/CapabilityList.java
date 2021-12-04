package tamaized.melongolem.common.capability;


import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
		if (e.getObject() instanceof Player) {
			e.addCapability(ITinyGolemCapability.ID, new ICapabilitySerializable<CompoundTag>() {

				ITinyGolemCapability inst = TINY_GOLEM.getDefaultInstance();

				@Nonnull
				@Override
				public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
					return TINY_GOLEM.orEmpty(capability, LazyOptional.of(() -> inst)).cast();
				}

				@Override
				public CompoundTag serializeNBT() {
					return (CompoundTag) TINY_GOLEM.getStorage().writeNBT(TINY_GOLEM, inst, null);
				}

				@Override
				public void deserializeNBT(CompoundTag nbt) {
					TINY_GOLEM.getStorage().readNBT(TINY_GOLEM, inst, null, nbt);
				}

			});
		}
	}

	@SubscribeEvent
	public static void updateClone(PlayerEvent.Clone e) {
		e.getPlayer().getCapability(TINY_GOLEM).ifPresent(cap -> e.getOriginal().getCapability(TINY_GOLEM).ifPresent(cap::copyFrom));
	}

}

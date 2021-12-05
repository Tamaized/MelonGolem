package tamaized.melongolem.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = MelonMod.MODID)
public class CapabilityList {

	public static final Capability<ITinyGolemCapability> TINY_GOLEM = CapabilityManager.get(new CapabilityToken<>(){});


	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(ITinyGolemCapability.class);
	}

	@SubscribeEvent
	public static void attachCapabilityEntity(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof Player) {
			e.addCapability(ITinyGolemCapability.ID, new ICapabilitySerializable<CompoundTag>() {

				LazyOptional<ITinyGolemCapability> inst = LazyOptional.of(TinyGolemCapabilityHandler::new);

				@Nonnull
				@Override
				public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
					return TINY_GOLEM.orEmpty(capability, inst.cast());
				}

				@Override
				public CompoundTag serializeNBT() {
					return inst.orElseThrow(NullPointerException::new).serializeNBT();
				}

				@Override
				public void deserializeNBT(CompoundTag nbt) {
					inst.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
				}

			});
		}
	}

	@SubscribeEvent
	public static void updateClone(PlayerEvent.Clone e) {
		e.getPlayer().getCapability(TINY_GOLEM).ifPresent(cap -> e.getOriginal().getCapability(TINY_GOLEM).ifPresent(cap::copyFrom));
	}
}

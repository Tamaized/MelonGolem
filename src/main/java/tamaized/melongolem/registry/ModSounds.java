package tamaized.melongolem.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;
import tamaized.regutil.RegUtil;
import tamaized.regutil.RegistryClass;

@Component
public class ModSounds {

	private final DeferredRegister<SoundEvent> REGISTRY = RegUtil.create(Registries.SOUND_EVENT);

	public final DeferredHolder<SoundEvent, SoundEvent> DADDY = REGISTRY.register(
		"melon_me_daddy",
		() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MelonMod.MODID, "melon_me_daddy"))
	);

}

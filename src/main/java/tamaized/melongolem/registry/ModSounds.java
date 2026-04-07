package tamaized.melongolem.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;
import tamaized.regutil.RegUtil;

@Component
public class ModSounds {

	private final DeferredRegister<SoundEvent> REGISTRY = RegUtil.create(Registries.SOUND_EVENT);

	public final DeferredHolder<SoundEvent, SoundEvent> DADDY = REGISTRY.register(
		"melon_me_daddy",
		() -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(MelonMod.MODID, "melon_me_daddy"))
	);

}

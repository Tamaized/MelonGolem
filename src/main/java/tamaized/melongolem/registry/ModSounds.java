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

	public final DeferredHolder<SoundEvent, SoundEvent> DADDY = RegUtil.register(Registries.SOUND_EVENT, "melon_me_daddy",
		SoundEvent::createVariableRangeEvent
	);

}

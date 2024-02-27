package tamaized.melongolem.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tamaized.melongolem.MelonMod;
import tamaized.regutil.RegUtil;
import tamaized.regutil.RegistryClass;

public class ModSounds implements RegistryClass {

	private static final DeferredRegister<SoundEvent> REGISTRY = RegUtil.create(Registries.SOUND_EVENT);

	public static final DeferredHolder<SoundEvent, SoundEvent> DADDY = REGISTRY.register("melon_me_daddy", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MelonMod.MODID, "melon_me_daddy")));

	@Override
	public void init(IEventBus bus) {

	}

}

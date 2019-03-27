package tamaized.melongolem;


import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MelonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MelonSounds {

	public static SoundEvent daddy = null;

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		daddy = registerSound(event, "melonmedaddy");
	}

	private static SoundEvent registerSound(RegistryEvent.Register<SoundEvent> event, @SuppressWarnings("SameParameterValue") String soundName) {
		SoundEvent sound = new SoundEvent(new ResourceLocation(MelonMod.MODID, soundName)).setRegistryName(soundName);
		event.getRegistry().register(sound);
		return sound;
	}
}
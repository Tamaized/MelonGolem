package tamaized.melongolem.client;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import tamaized.beanification.Component;

@Component(dist = Dist.CLIENT)
public class NarratorHelper {

	private final Narrator narrator = Narrator.getNarrator();

	public void say(String text) {
		if (!narrator.active())
			return;
		narrator.clear();
		narrator.say(text, false, Minecraft.getInstance().options.getFinalSoundSourceVolume(SoundSource.VOICE));
	}

}

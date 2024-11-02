package tamaized.melongolem.client;

import com.mojang.text2speech.Narrator;
import net.neoforged.api.distmarker.Dist;
import tamaized.beanification.Component;

@Component(dist = Dist.CLIENT)
public class NarratorHelper {

	private final Narrator narrator = Narrator.getNarrator();

	public void say(String text) {
		if (!narrator.active())
			return;
		narrator.clear();
		narrator.say(text, false);
	}

}

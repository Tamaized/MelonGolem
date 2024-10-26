package tamaized.melongolem.config;

import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;

@Component
public class ConfigUtil {

	public String translationKey(String key) {
		return MelonMod.MODID + ".config." + key;
	}

}

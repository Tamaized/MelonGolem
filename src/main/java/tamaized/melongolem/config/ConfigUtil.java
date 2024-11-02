package tamaized.melongolem.config;

import tamaized.beanification.Bean;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;

@Component
public class ConfigUtil {

	private final String suffix;

	@SuppressWarnings("unused")
	public ConfigUtil() {
		this(null);
	}

	public ConfigUtil(String suffix) {
		this.suffix = suffix;
	}

	public String translationKey(String key) {
		return MelonMod.MODID + ".config." + (suffix == null ? "" : (suffix + ".")) + key;
	}

	@Bean("common")
	private static ConfigUtil commonPrefix() {
		return new ConfigUtil("common");
	}

	@Bean("client")
	private static ConfigUtil clientPrefix() {
		return new ConfigUtil("client");
	}

	@Bean("donatorSettings")
	private static ConfigUtil configUtil() {
		return new ConfigUtil("client.donatorSettings");
	}

}

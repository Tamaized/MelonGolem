package tamaized.melongolem.config.client;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.beanification.PostConstruct;

@Component
public class ClientConfig {

	@Autowired
	private DonatorSettings donatorSettings;

	private boolean dirty = true;

	public ModConfigSpec.BooleanValue tehnutMode;
	public ModConfigSpec.BooleanValue tts;

	@PostConstruct
	private void postConstruct() {
		ModConfigSpec spec = new ModConfigSpec.Builder().configure(this::setup).getRight();
		ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, spec);
	}

	private ClientConfig setup(ModConfigSpec.Builder builder) {
		donatorSettings.setup(builder);

		tehnutMode = builder
			.translation("TehNut Mode") // TODO: translation key
			.comment(":^)")
			.define("tehnutMode", false);

		tts = builder
			.translation("TTS Signs") // TODO: translation key
			.comment("When enabled, written signs on a golem's head will play text to speech audio")
			.define("tts", true);

		return this;
	}

}

package tamaized.melongolem.config.client;

import net.neoforged.neoforge.common.ModConfigSpec;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.config.ConfigUtil;

@Component
public class DonatorSettings {

	@Autowired
	private ConfigUtil configUtil;

	public ModConfigSpec.BooleanValue enable;
	public ModConfigSpec.IntValue color;

	void setup(ModConfigSpec.Builder builder) {
		builder.comment("Donator Settings").push("Donator Settings");
		{
			enable = builder
				.translation(configUtil.translationKey("enable"))
				.comment("Enables donator settings for yourself")
				.define("enable", true);

			color = builder
				.translation(configUtil.translationKey("color"))
				.comment("Changes the Tiny Melon Golem Color")
				.defineInRange("color", 0xFFA4EA, Integer.MIN_VALUE, Integer.MAX_VALUE);

		}
		builder.pop();
	}

}

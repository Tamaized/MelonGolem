package tamaized.melongolem.config.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.beanification.PostConstruct;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.config.ConfigUtil;

@Component
public class DonatorSettings {

	@Autowired("donatorSettings")
	private ConfigUtil configUtil;

	public ModConfigSpec.BooleanValue enable;
	public ModConfigSpec.IntValue color;

	private boolean dirty;

	@PostConstruct
	private void postConstruct(IEventBus modBus) {
		modBus.addListener(ModConfigEvent.Reloading.class, event -> {
			if (event.getConfig().getType() == ModConfig.Type.CLIENT && event.getConfig().getModId().equals(MelonMod.MODID)) {
				markDirty();
			}
		});
	}

	public boolean isDirty() {
		return dirty;
	}

	public void markDirty() {
		dirty = true;
	}

	public void unmarkDirty() {
		dirty = false;
	}

	void setup(ModConfigSpec.Builder builder) {
		builder.comment("Settings for players who have donated or contributed to the Mod's development").push("donatorSettings");
		{
			enable = builder
				.translation(configUtil.translationKey("enable"))
				.comment("Enables donator settings for yourself")
				.define("enable", true);

			color = builder
				.translation(configUtil.translationKey("color"))
				.comment("Changes the Tiny Melon Golem Color, values are in RRGGBB hex. Use a Hex Color to Decimal converter tool")
				.defineInRange("color", 0xFFA4EA, Integer.MIN_VALUE, Integer.MAX_VALUE);

		}
		builder.pop();
	}

}

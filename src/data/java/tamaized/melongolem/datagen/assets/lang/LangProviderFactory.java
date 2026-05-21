package tamaized.melongolem.datagen.assets.lang;

import net.minecraft.locale.Language;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.beanification.Directory;
import tamaized.datagenutil.assets.lang.ExtendedLangProvider;
import tamaized.datagenutil.assets.lang.LangProvider;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.registry.ModEntities;

import java.util.List;

@Component
public class LangProviderFactory {

	@Autowired
	private ModEntities entities;

	@Directory(value = LangProvider.class)
	private List<LangProvider> langProviders;

	public LanguageProvider make(GatherDataEvent event) {
		return new ExtendedLangProvider(
			event.getGenerator().getPackOutput(),
			MelonMod.MODID,
			Language.DEFAULT,
			langProviders
		) {
			@Override
			protected void addAdditionalTranslations() {
				addCreativeTab("Melon Golem");

				addEntityTypeWithSpawnEgg(entities.MELON_GOLEM, entities.SPAWN_EGG_MELON_GOLEM, "Melon Golem");
				addEntityType(entities.MELON_SLICE, "Melon Slice");
				addEntityType(entities.TINY_MELON_GOLEM, "Tiny Melon Golem");
				addEntityTypeWithSpawnEgg(entities.GLISTERING_MELON_GOLEM, entities.SPAWN_EGG_GLISTERING_MELON_GOLEM, "Glistering Melon Golem");

				addCommonConfig("health", "Melon Golem Base Health");
				addCommonConfig("glister_damage_amp", "Glistering Melon Slice Projectile Damage Multiplier");
				addCommonConfig("hats", "Hats");
				addCommonConfig("shear", "Non-Destructive Hat shearing");
				addCommonConfig("eats", "Cannibalistic Golems");
				addCommonConfig("heal", "Cannibalistic Health Restore Amount");
				addCommonConfig("stabby", "Items used to create Melon Golems");

				addClientConfig("tehnut", "TehNut Mode");
				addClientConfig("tts", "Text-to-Speech Signs");

				addConfiguration("donatorSettings", "Donor Settings");
				addClientConfig("donatorSettings.color", "Color");
				addClientConfig("donatorSettings.enable", "Enabled");
			}
		};
	}

}

package tamaized.melongolem.datagen.lang;

import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.registry.ModBlocks;
import tamaized.melongolem.registry.ModEntities;
import tamaized.melongolem.registry.ModItems;

import java.util.function.Supplier;

@Component
public class LangProviderFactory {

	@Autowired
	private ModEntities entities;

	@Autowired
	private ModItems items;

	@Autowired
	private ModBlocks blocks;

	public LanguageProvider make(GatherDataEvent event) {
		return new LanguageProvider(
			event.getGenerator().getPackOutput(),
			MelonMod.MODID,
			Language.DEFAULT
		) {
			@Override
			protected void addTranslations() {
				addCreativeTab("Melon Golem");

				addEntityTypeWithSpawnEgg(entities.MELON_GOLEM, entities.SPAWN_EGG_MELON_GOLEM, "Melon Golem");
				addEntityType(entities.MELON_SLICE, "Melon Slice");
				addEntityType(entities.TINY_MELON_GOLEM, "Tiny Melon Golem");
				addEntityTypeWithSpawnEgg(entities.GLISTERING_MELON_GOLEM, entities.SPAWN_EGG_GLISTERING_MELON_GOLEM, "Glistering Melon Golem");

				addItem(items.MELON_STICK, "Melon on a Stick");

				addBlock(blocks.GLISTERING_MELON, "Glistering Melon");

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

			private void addCreativeTab(String translation) {
				add(MelonMod.MODID + ".item_group", translation);
			}

			private void addEntityTypeWithSpawnEgg(Supplier<? extends EntityType<? extends Entity>> entity, Supplier<Item> spawnEgg, String translation) {
				addEntityType(entity, translation);
				addItem(spawnEgg, translation.concat(" Spawn Egg"));
			}

			private void addDeathMessage(ResourceKey<DamageType> key, String translation) {
				add(key.location().toLanguageKey("death.attack"), translation);
			}

			private void addSubtitle(SoundEvent key, String translation) {
				add(key.getLocation().toLanguageKey("subtitles"), translation);
			}

			private void addConfiguration(String configuration, String translation) {
				add(MelonMod.MODID + ".configuration." + configuration, translation);
			}

			private void addConfig(String config, String translation) {
				add(MelonMod.MODID + ".config." + config, translation);
			}

			private void addCommonConfig(String config, String translation) {
				addConfig("common." + config, translation);
			}

			private void addClientConfig(String config, String translation) {
				addConfig("client." + config, translation);
			}
		};
	}

}

package tamaized.melongolem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Objects;

public class MelonConfig {

	public static class Client {
		public static boolean dirty = true;
		public final DonatorSettings DONATOR_SETTINGS = new DonatorSettings();

		public ModConfigSpec.BooleanValue tehnutMode;
		public ModConfigSpec.BooleanValue tts;

		public Client(ModConfigSpec.Builder builder) {
			builder.
					comment("Donator Settings").
					push("Donator Settings");
			{
				DONATOR_SETTINGS.enable = builder.
						translation(translation("enable")).
						comment("Enables donator settings for yourself").
						define("enable", true);

				DONATOR_SETTINGS.color = builder.
						translation(translation("color")).
						comment("Changes the Tiny Melon Golem Color").
						defineInRange("color", 0xFFA4EA, Integer.MIN_VALUE, Integer.MAX_VALUE);

			}
			builder.pop();

			tehnutMode = builder.
					translation("TehNut Mode").
					comment(":^)").
					define("tehnutMode", false);

			tts = builder.
					translation("TTS Signs").
					comment("When enabled, written signs on a golem's head will play text to speech audio").
					define("tts", true);
		}

		public static class DonatorSettings {
			public ModConfigSpec.BooleanValue enable;
			public ModConfigSpec.IntValue color;
		}

	}

	public static Item stabItem = Items.STICK;
	public ModConfigSpec.DoubleValue health;
	public ModConfigSpec.DoubleValue damage;
	public ModConfigSpec.DoubleValue glisterDamageAmp;
	public ModConfigSpec.BooleanValue hats;
	public ModConfigSpec.BooleanValue shear;
	public ModConfigSpec.BooleanValue eats;
	public ModConfigSpec.DoubleValue heal;
	public ModConfigSpec.ConfigValue<String> stabby;

	public MelonConfig(ModConfigSpec.Builder builder) {

		health = builder.
				translation("Base Golem Health").
				comment("").
				defineInRange("health", 8.0F, 0.5F, Float.MAX_VALUE);

		damage = builder.
				translation("Melon Slice Damage").
				comment("").
				defineInRange("damage", 4.0F, 0.5F, Float.MAX_VALUE);

		glisterDamageAmp = builder.
				translation("Glistering Melon Slice Damage Amplification").
				comment("").
				defineInRange("damage", 1.5F, 1F, Float.MAX_VALUE);

		hats = builder.
				translation("Enable Golem Block Heads").
				comment("Enables the placement of blocks on golems' heads").
				define("hats", true);

		shear = builder.
				translation("Shears Spawn Block").
				comment("If disabled, shearing a golem destroys the block").
				define("shear", true);

		eats = builder.
				translation("Golem Eats Melons").
				comment("If enabled, golems will hunt for nearby melon slices to replensih health").
				define("eats", true);

		heal = builder.
				translation("Melon Heal Amount").
				comment("The amount a golem will heal for after consuming a melon slice").
				defineInRange("heal", 1.0F, 0.5F, Float.MAX_VALUE);


		stabby = builder.
				translation("Stabby Life Item").
				comment("The item used to spawn a melon golem\n\n[domain:name] domain will default to `minecraft`").
				define("stabby", Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(Items.STICK)).getPath());
	}

	public static void setupStabby() {
		String[] split = MelonMod.config.stabby.get().split(":");
		String domain = "minecraft";
		String regname = split[0];
		if (split.length > 1) {
			domain = split[0];
			regname = split[1];
		}
		Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(domain, regname));
		if (item == null || item instanceof AirItem)
			return;
		stabItem = item;
	}

	public static boolean compareStabbyItem(ItemStack stack) {
		return stack.getItem() == stabItem;
	}

	public static void init(IEventBus modBus) {
		modBus.addListener(ModConfigEvent.Reloading.class, event -> {
			if (event.getConfig().getModId().equals(MelonMod.MODID)) {
				setupStabby();
			}
		});
		modBus.addListener(FMLLoadCompleteEvent.class, event -> {
			setupStabby();
		});
	}

	private static String translation(String key) {
		return MelonMod.MODID + ".config." + key;
	}

}

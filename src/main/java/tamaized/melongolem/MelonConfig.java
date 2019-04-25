package tamaized.melongolem;


import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = MelonMod.MODID)
public class MelonConfig {

	public ForgeConfigSpec.DoubleValue health;
	public static boolean dirty = true;
	public static Item stabItem = Items.STICK;
	public final DonatorSettings DONATOR_SETTINGS = new DonatorSettings();
	public ForgeConfigSpec.DoubleValue damage;
	public ForgeConfigSpec.DoubleValue glisterDamageAmp;
	public ForgeConfigSpec.BooleanValue hats;
	public ForgeConfigSpec.BooleanValue shear;
	public ForgeConfigSpec.BooleanValue eats;
	public ForgeConfigSpec.DoubleValue heal;
	public ForgeConfigSpec.BooleanValue tehnutMode;
	public ForgeConfigSpec.BooleanValue tts;
	public ForgeConfigSpec.ConfigValue<String> stabby;

	public MelonConfig(ForgeConfigSpec.Builder builder) {
		builder.
				comment("Donator Settings").
				push("Donator Settings");
		{
			DONATOR_SETTINGS.enable = builder.
					translation("Enable").
					comment("Enables donator settings for yourself").
					define("enable", true);

			DONATOR_SETTINGS.color = builder.
					translation("Color").
					comment("Sets the mini-golem color for yourself (0xRRGGBB)").
					define("color", "0xFFFFFF");

		}
		builder.pop();

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

		tehnutMode = builder.
				translation("TehNut Mode").
				comment(":^)").
				define("tehnutMode", false);

		tts = builder.
				translation("TTS Signs").
				comment("When enabled, written signs on a golem's head will play text to speech audio").
				define("tts", true);

		stabby = builder.
				translation("Stabby Life Item").
				comment("The item used to spawn a melon golem\n\n[domain:name] domain will default to `minecraft`").
				define("stabby", Objects.requireNonNull(Items.STICK.getRegistryName()).getPath());
	}

	public static void setupStabby() {
		String[] split = MelonMod.config.stabby.get().split(":");
		String domain = "minecraft";
		String regname = split[0];
		if (split.length > 1) {
			domain = split[0];
			regname = split[1];
		}
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(domain, regname));
		if (item == null || item instanceof ItemAir)
			return;
		stabItem = item;
	}

	public static boolean compareStabbyItem(ItemStack stack){
		return stack.getItem() == stabItem;
	}

	/*@SubscribeEvent TODO
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(MelonMod.MODID)) {
			ConfigManager.sync(MelonMod.MODID, Config.Type.INSTANCE);
			setupStabby();
			setupColor();
		}
	}*/

	public static void setupColor() {
		try {
			MelonMod.config.DONATOR_SETTINGS.colorint = Integer.decode(MelonMod.config.DONATOR_SETTINGS.color.get());
		} catch (Throwable e) {
			//			MelonMod.config.DONATOR_SETTINGS.color = "0xFFFFFF"; TODO
			MelonMod.config.DONATOR_SETTINGS.colorint = 0xFFFFFF;
		}
		dirty = true;
	}

	public static class DonatorSettings {
		public ForgeConfigSpec.BooleanValue enable;
		public ForgeConfigSpec.ConfigValue<String> color;
		public int colorint = 0xFFFFFF;
	}

}

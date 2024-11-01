package tamaized.melongolem.config.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.beanification.PostConstruct;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.config.ConfigUtil;

import java.util.Objects;

@Component
public class CommonConfig {

	@Autowired
	private ConfigUtil configUtil;

	private Item stabItem = Items.STICK;
	public ModConfigSpec.DoubleValue health;
	public ModConfigSpec.DoubleValue damage;
	public ModConfigSpec.DoubleValue glisterDamageAmp;
	public ModConfigSpec.BooleanValue hats;
	public ModConfigSpec.BooleanValue shear;
	public ModConfigSpec.BooleanValue eats;
	public ModConfigSpec.DoubleValue heal;
	public ModConfigSpec.ConfigValue<String> stabby;

	@PostConstruct
	private void postConstruct(IEventBus modBus) {
		ModConfigSpec spec = new ModConfigSpec.Builder().configure(this::setup).getRight();
		ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, spec);
		ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> ConfigurationScreen::new);

		modBus.addListener(ModConfigEvent.Reloading.class, event -> {
			if (event.getConfig().getModId().equals(MelonMod.MODID)) {
				setupStabby();
			}
		});
		modBus.addListener(FMLLoadCompleteEvent.class, event -> setupStabby());
	}

	private CommonConfig setup(ModConfigSpec.Builder builder) {
		health = builder
			.translation(configUtil.translationKey("health"))
			.comment("How much base max health Melon Golems will have")
			.defineInRange("health", 8.0F, 0.5F, Float.MAX_VALUE);

		damage = builder
			.translation(configUtil.translationKey("damage"))
			.comment("How much damage projectiles fired by Melon Golems will deal")
			.defineInRange("damage", 4.0F, 0.5F, Float.MAX_VALUE);

		glisterDamageAmp = builder
			.translation(configUtil.translationKey("glister_damage_amp"))
			.comment("Damage multiplier for projectiles fired by Glistering Melon Golems")
			.defineInRange("damage", 1.5F, 1F, Float.MAX_VALUE);

		hats = builder
			.translation(configUtil.translationKey("hats"))
			.comment("Enables the ability to place Blocks onto a Melon Golem's head")
			.define("hats", true);

		shear = builder
			.translation(configUtil.translationKey("shear"))
			.comment("If disabled, shearing a Melon Golem will destroy the Block on its head")
			.define("shear", true);

		eats = builder
			.translation(configUtil.translationKey("eats"))
			.comment("If enabled, Melon Golems will hunt for nearby melon slices to replenish health")
			.define("eats", true);

		heal = builder
			.translation(configUtil.translationKey("heal"))
			.comment("The amount health a Melon Golem will gain when consuming a melon slice")
			.defineInRange("heal", 1.0F, 0.5F, Float.MAX_VALUE);


		stabby = builder
			.translation(configUtil.translationKey("stabby"))
			.comment("The item used in each hand to spawn a melon golem. Format as `namespace:name`")
			.define("stabby", Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(Items.STICK)).toString());

		return this;
	}

	private void setupStabby() {
		String[] split = stabby.get().split(":");
		String domain = "minecraft";
		String regname = split[0];
		if (split.length > 1) {
			domain = split[0];
			regname = split[1];
		}
		Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(domain, regname));
		stabItem = item instanceof AirItem ? Items.STICK : item;
	}

	public boolean compareStabbyItem(ItemStack stack) {
		return stack.getItem() == stabItem;
	}

}

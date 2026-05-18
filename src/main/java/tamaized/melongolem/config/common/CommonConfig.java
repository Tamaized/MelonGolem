package tamaized.melongolem.config.common;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.beanification.PostConstruct;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.config.ConfigUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class CommonConfig {

	@Autowired("common")
	private ConfigUtil configUtil;

	private List<Item> stabItems = List.of(Items.STICK);

	@Nullable
	private ModConfigSpec.DoubleValue health;

	@Nullable
	private ModConfigSpec.DoubleValue damage;

	@Nullable
	private ModConfigSpec.DoubleValue glisterDamageAmp;

	@Nullable
	private ModConfigSpec.BooleanValue hats;

	@Nullable
	private ModConfigSpec.BooleanValue shear;

	@Nullable
	private ModConfigSpec.BooleanValue eats;

	@Nullable
	private ModConfigSpec.DoubleValue heal;

	@Nullable
	private ModConfigSpec.ConfigValue<List<? extends String>> stabby;

	@PostConstruct
	private void postConstruct(IEventBus modBus) {
		ModConfigSpec spec = new ModConfigSpec.Builder().configure(this::setup).getRight();
		ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, spec);

		modBus.addListener(ModConfigEvent.Reloading.class, event -> {
			if (event.getConfig().getType() == ModConfig.Type.COMMON && event.getConfig().getModId().equals(MelonMod.MODID)) {
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
			.defineListAllowEmpty("stabby",
				List.of(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(Items.STICK)).toString()),
				() -> BuiltInRegistries.ITEM.getKey(Items.STICK).toString(),
				o -> o instanceof String);

		return this;
	}

	public  Optional<ModConfigSpec.DoubleValue> getHealth() {
		return Optional.ofNullable(health);
	}

	public  Optional<ModConfigSpec.DoubleValue> getDamage() {
		return Optional.ofNullable(damage);
	}

	public  Optional<ModConfigSpec.DoubleValue> getGlisterDamageAmp() {
		return Optional.ofNullable(glisterDamageAmp);
	}

	public  Optional<ModConfigSpec.BooleanValue> getHats() {
		return Optional.ofNullable(hats);
	}

	public  Optional<ModConfigSpec.BooleanValue> getShear() {
		return Optional.ofNullable(shear);
	}

	public  Optional<ModConfigSpec.BooleanValue> getEats() {
		return Optional.ofNullable(eats);
	}

	public  Optional<ModConfigSpec.DoubleValue> getHeal() {
		return Optional.ofNullable(heal);
	}

	private void setupStabby() {
		if (stabby == null)
			return;

		stabItems = stabby.get().stream().map(stab -> {
			String[] split = stab.split(":");
			String domain = "minecraft";
			String regname = split[0];
			if (split.length > 1) {
				domain = split[0];
				regname = split[1];
			}
			Optional<Holder.Reference<Item>> item = BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath(domain, regname));
			if (item.isEmpty() || item.get().value() instanceof AirItem) {
				return Items.STICK;
			}
			return item.get().value();
		}).toList();
	}

	public boolean compareStabbyItem(ItemStack stack) {
		return stabItems.contains(stack.getItem());
	}

}

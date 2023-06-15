package tamaized.melongolem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tamaized.melongolem.client.MelonConfigScreen;
import tamaized.melongolem.common.*;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.network.NetworkMessages;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod(MelonMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MelonMod {

	public static final String MODID = "melongolem";

	public static MelonConfig config;
	public static MelonConfig.Client configClient;

	public static final SimpleChannel network = NetworkRegistry.ChannelBuilder.
			named(new ResourceLocation(MODID, MODID)).
			clientAcceptedVersions(s -> true).
			serverAcceptedVersions(s -> true).
			networkProtocolVersion(() -> "1").
			simpleChannel();

	public static final Logger logger = LogManager.getLogger(MODID);

	private static final Map<EntityType<? extends LivingEntity>, Supplier<AttributeSupplier.Builder>> attributes = new HashMap<>();

	private static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	private static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
	private static final DeferredRegister<EntityType<?>> ENTITY_REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
	public static final DeferredRegister<SoundEvent> SOUND_REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

	public static final RegistryObject<Item> ITEM_MELON_STICK = ITEM_REGISTRY
			.register("melonstick", () -> new ItemMelonStick(new Item.Properties()));

	public static final RegistryObject<Block> BLOCK_GLISTERING_MELON = BLOCK_REGISTRY
			.register("glisteringmelonblock", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).pushReaction(PushReaction.DESTROY).strength(1.0F).sound(SoundType.WOOD).lightLevel(state -> 4)));
	public static final RegistryObject<BlockItem> ITEMBLOCK_GLISTERING_MELON = ITEM_REGISTRY
			.register("glisteringmelonblock", () -> new BlockItem(BLOCK_GLISTERING_MELON.get(), new Item.Properties().setNoRepair()));

	public static final RegistryObject<EntityType<? extends AbstractGolem>> ENTITY_TYPE_MELON_GOLEM = ENTITY_REGISTRY
			.register("entitymelongolem", () -> assign(EntityMelonGolem.class, 0.7F, 1.9F, 128, 1, true, MobCategory.CREATURE, EntityMelonGolem::_registerAttributes));

	public static final RegistryObject<EntityType<? extends AbstractGolem>> ENTITY_TYPE_GLISTERING_MELON_GOLEM = ENTITY_REGISTRY
			.register("entityglisteringmelongolem", () -> assign(EntityGlisteringMelonGolem.class, 0.7F, 1.9F, 128, 1, true, MobCategory.CREATURE, EntityMelonGolem::_registerAttributes));

	public static final RegistryObject<EntityType<? extends EntityMelonSlice>> ENTITY_TYPE_MELON_SLICE = ENTITY_REGISTRY
			.register("entitymelonslice", () -> assign(EntityMelonSlice.class, 0.25F, 0.25F, 128, 1, true, MobCategory.MISC));

	public static final RegistryObject<EntityType<? extends TamableAnimal>> ENTITY_TYPE_TINY_MELON_GOLEM = ENTITY_REGISTRY
			.register("entitytinymelongolem", () -> assign(EntityTinyMelonGolem.class, 0.175F, 0.475F, 128, 1, true, MobCategory.CREATURE, EntityMelonGolem::_registerAttributes));

	public static final RegistryObject<Item> ITEM_SPAWN_EGG_MELON_GOLEM = ITEM_REGISTRY
			.register("melongolemspawnegg", () -> new ForgeSpawnEggItem(ENTITY_TYPE_MELON_GOLEM, 0x00FF00, 0x000000, new Item.Properties()));

	public static final RegistryObject<Item> ITEM_SPAWN_EGG_GLISTERING_MELON_GOLEM = ITEM_REGISTRY
			.register("glisteringmelongolemspawnegg", () -> new ForgeSpawnEggItem(ENTITY_TYPE_GLISTERING_MELON_GOLEM, 0xAAFF00, 0xFFCC00, new Item.Properties()));

	public static final RegistryObject<SoundEvent> DADDY = SOUND_REGISTRY
			.register("melonmedaddy", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MelonMod.MODID, "melonmedaddy")));

	public MelonMod() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEM_REGISTRY.register(modBus);
		BLOCK_REGISTRY.register(modBus);
		ENTITY_REGISTRY.register(modBus);
		SOUND_REGISTRY.register(modBus);
		{
			final Pair<MelonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(MelonConfig::new);
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, specPair.getRight());
			config = specPair.getLeft();
		}
		{
			final Pair<MelonConfig.Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(MelonConfig.Client::new);
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
			configClient = specPair.getLeft();
		}
		DonatorHandler.start();
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(MelonConfigScreen::new));
	}

	@SubscribeEvent
	public static void addStuffToTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
			event.getEntries().putAfter(new ItemStack(Items.MELON), new ItemStack(ITEMBLOCK_GLISTERING_MELON.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		} else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.getEntries().putAfter(new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK), new ItemStack(ITEM_MELON_STICK.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		} else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			event.getEntries().putAfter(new ItemStack(Items.MAGMA_CUBE_SPAWN_EGG), new ItemStack(ITEM_SPAWN_EGG_MELON_GOLEM.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.getEntries().putBefore(new ItemStack(Items.GLOW_SQUID_SPAWN_EGG), new ItemStack(ITEM_SPAWN_EGG_GLISTERING_MELON_GOLEM.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		}
	}


	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		attributes.forEach((type, attribute) -> event.put(type, attribute.get().build()));
	}

	private static <T extends LivingEntity> EntityType<T> assign(Class<T> entity, float w, float h, int range, int freq, boolean updates, MobCategory classification, Supplier<AttributeSupplier.Builder> attributes) {
		EntityType<T> type = assign(entity, w, h, range, freq, updates, classification);
		MelonMod.attributes.put(type, attributes);
		return type;
	}

	private static <T extends Entity> EntityType<T> assign(Class<T> entity, float w, float h, int range, int freq, boolean updates, MobCategory classification) {
		final String name = entity.getSimpleName().toLowerCase();
		return EntityType.Builder.<T>of((et, world) -> {
			try {
				return entity.getConstructor(Level.class).newInstance(world);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			return null;
		}, classification).
				setTrackingRange(range).
				setUpdateInterval(freq).
				setShouldReceiveVelocityUpdates(updates).
				sized(w, h).
				build(name);
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		NetworkMessages.register(network);
	}

	@SubscribeEvent
	public static void init(FMLLoadCompleteEvent event) {
		MelonConfig.setupStabby();
		MelonConfig.setupColor();
	}
}

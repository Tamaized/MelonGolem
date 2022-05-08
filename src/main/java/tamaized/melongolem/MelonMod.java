package tamaized.melongolem;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tamaized.melongolem.client.ClientListener;
import tamaized.melongolem.client.MelonConfigScreen;
import tamaized.melongolem.common.EntityGlisteringMelonGolem;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.common.EntityMelonSlice;
import tamaized.melongolem.common.EntityTinyMelonGolem;
import tamaized.melongolem.common.ItemMelonStick;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.network.NetworkMessages;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
	private static final DeferredRegister<EntityType<?>> ENTITY_REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

	public static final RegistryObject<Item> ITEM_MELON_STICK = ITEM_REGISTRY
			.register("melonstick", () -> new ItemMelonStick(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

	public static final RegistryObject<Block> BLOCK_GLISTERING_MELON = BLOCK_REGISTRY
			.register("glisteringmelonblock", () -> new Block(Block.Properties.of(Material.VEGETABLE, MaterialColor.COLOR_LIGHT_GREEN).strength(1.0F).sound(SoundType.WOOD).lightLevel(state -> 4)));
	public static final RegistryObject<BlockItem> ITEMBLOCK_GLISTERING_MELON = ITEM_REGISTRY
			.register("glisteringmelonblock", () -> new BlockItem(BLOCK_GLISTERING_MELON.get(), new Item.Properties().setNoRepair().tab(CreativeModeTab.TAB_MISC)));

	public static final RegistryObject<EntityType<? extends AbstractGolem>> ENTITY_TYPE_MELON_GOLEM = ENTITY_REGISTRY
			.register("entitymelongolem", () -> assign(EntityMelonGolem.class, 0.7F, 1.9F, 128, 1, true, MobCategory.CREATURE, EntityMelonGolem::_registerAttributes));

	public static final RegistryObject<EntityType<? extends AbstractGolem>> ENTITY_TYPE_GLISTERING_MELON_GOLEM = ENTITY_REGISTRY
			.register("entityglisteringmelongolem", () -> assign(EntityGlisteringMelonGolem.class, 0.7F, 1.9F, 128, 1, true, MobCategory.CREATURE, EntityMelonGolem::_registerAttributes));

	public static final RegistryObject<EntityType<? extends EntityMelonSlice>> ENTITY_TYPE_MELON_SLICE = ENTITY_REGISTRY
			.register("entitymelonslice", () -> assign(EntityMelonSlice.class, 0.25F, 0.25F, 128, 1, true, MobCategory.MISC));

	public static final RegistryObject<EntityType<? extends TamableAnimal>> ENTITY_TYPE_TINY_MELON_GOLEM = ENTITY_REGISTRY
			.register("entitytinymelongolem", () -> assign(EntityTinyMelonGolem.class, 0.175F, 0.475F, 128, 1, true, MobCategory.CREATURE, EntityMelonGolem::_registerAttributes));

	public static final RegistryObject<Item> ITEM_SPAWN_EGG_MELON_GOLEM = ITEM_REGISTRY
			.register("melongolemspawnegg", () -> new ForgeSpawnEggItem(ENTITY_TYPE_MELON_GOLEM, 0x00FF00, 0x000000, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

	public static final RegistryObject<Item> ITEM_SPAWN_EGG_GLISTERING_MELON_GOLEM = ITEM_REGISTRY
			.register("glisteringmelongolemspawnegg", () -> new ForgeSpawnEggItem(ENTITY_TYPE_GLISTERING_MELON_GOLEM, 0xAAFF00, 0xFFCC00, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

	public static final ImmutableSet<Item> SIGNS = ImmutableSet.of(Items.ACACIA_SIGN, Items.BIRCH_SIGN, Items.DARK_OAK_SIGN, Items.JUNGLE_SIGN, Items.JUNGLE_SIGN, Items.OAK_SIGN, Items.SPRUCE_SIGN);

	public MelonMod() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEM_REGISTRY.register(modBus);
		BLOCK_REGISTRY.register(modBus);
		ENTITY_REGISTRY.register(modBus);
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
		ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory(MelonConfigScreen::new));
	}


	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		attributes.forEach((type, attribute) -> event.put(type, attribute.get().build()));
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent e) {
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientListener::registerRenders);
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

	private static <T> T getNull() {
		return null;
	}
}

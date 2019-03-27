package tamaized.melongolem;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tamaized.melongolem.client.RenderMelonGolem;
import tamaized.melongolem.client.RenderMelonSlice;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.common.EntityMelonSlice;
import tamaized.melongolem.common.EntityTinyMelonGolem;
import tamaized.melongolem.common.ItemMelonStick;
import tamaized.melongolem.common.capability.ITinyGolemCapability;
import tamaized.melongolem.common.capability.TinyGolemCapabilityHandler;
import tamaized.melongolem.common.capability.TinyGolemCapabilityStorage;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.network.NetworkMessages;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Supplier;

@Mod(MelonMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MelonMod {

	public static final String MODID = "melongolem";

	public static final IModProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

	public static final MelonConfig config = ((Supplier<MelonConfig>) () -> {
		final Pair<MelonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(MelonConfig::new);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, specPair.getRight());
		return specPair.getLeft();
	}).get();

	public static final SimpleChannel network = NetworkRegistry.ChannelBuilder.
			named(new ResourceLocation(MODID, MODID)).
			clientAcceptedVersions(s -> true).
			serverAcceptedVersions(s -> true).
			networkProtocolVersion(() -> "1").
			simpleChannel();

	public static final Logger logger = LogManager.getLogger(MODID);

	@ObjectHolder(MelonMod.MODID + ":melonstick")
	public static final Item melonStick = Items.AIR;

	public static final EntityType entityTypeMelonGolem = assign(EntityMelonGolem.class, 128, 1, true);

	@ObjectHolder(MelonMod.MODID + ":entitymelonslice")
	public static final EntityType entityTypeMelonSlice = getNull();

	@ObjectHolder(MelonMod.MODID + ":entitytinymelongolem")
	public static final EntityType entityTypeTinyMelonGolem = getNull();

	public MelonMod() {
		DonatorHandler.start();
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> e) {
		e.getRegistry().registerAll(

				entityTypeMelonGolem,

				assign(EntityMelonSlice.class, 128, 1, true),

				assign(EntityTinyMelonGolem.class, 128, 1, true)

		);
	}

	@SubscribeEvent
	public static void registerRenders(FMLClientSetupEvent e) {
		RenderingRegistry.registerEntityRenderingHandler(EntityMelonGolem.class, RenderMelonGolem.Factory::normal);
		RenderingRegistry.registerEntityRenderingHandler(EntityMelonSlice.class, RenderMelonSlice::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTinyMelonGolem.class, RenderMelonGolem.Factory::tiny);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e) {
		e.getRegistry().registerAll(

				assign(new ItemMelonStick(new Item.Properties().group(ItemGroup.MISC)), "melonstick"),

				assign(new ItemSpawnEgg(entityTypeMelonGolem, 0xFF00, 0x0, new Item.Properties().group(ItemGroup.MISC)), "melongolemspawnegg")

		);
	}

	private static Item assign(Item item, String name) {
		return item

				.setRegistryName(MODID, name);
	}

	private static <T extends Entity> EntityType<T> assign(Class<T> entity, int range, int freq, boolean updates) {
		final String name = entity.getSimpleName().toLowerCase();
		EntityType<T> type = EntityType.Builder.create(entity, world -> {
			try {
				return entity.getConstructor(World.class).newInstance(world);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			return null;
		}).
				tracker(range, freq, updates).
				build(name);
		type.setRegistryName(MODID, name);
		return type;
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(ITinyGolemCapability.class, new TinyGolemCapabilityStorage(), TinyGolemCapabilityHandler::new);

		NetworkMessages.register(network);

		proxy.init();
	}

	@SubscribeEvent
	public static void init(FMLLoadCompleteEvent event) {
		MelonConfig.setupStabby();
		MelonConfig.setupColor();

		proxy.finish();
	}

	private static <T> T getNull() {
		return null;
	}

}

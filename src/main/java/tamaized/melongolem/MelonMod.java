package tamaized.melongolem;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.common.EntityMelonSlice;
import tamaized.melongolem.common.EntityTinyMelonGolem;
import tamaized.melongolem.common.ItemMelonStick;
import tamaized.melongolem.common.capability.ITinyGolemCapability;
import tamaized.melongolem.common.capability.TinyGolemCapabilityHandler;
import tamaized.melongolem.common.capability.TinyGolemCapabilityStorage;
import tamaized.melongolem.network.NetworkMessages;

@Mod.EventBusSubscriber
@Mod(modid = MelonMod.MODID, name = "EntityMelonGolem", version = MelonMod.version, acceptedMinecraftVersions = "[1.12,)")
public class MelonMod {

	@GameRegistry.ObjectHolder(MelonMod.MODID + ":melonstick")
	public static final Item melonStick = Items.AIR;

	public final static String version = "${version}";
	public static final String MODID = "melongolem";
	@Instance(MODID)
	public static MelonMod instance = new MelonMod();
	@SidedProxy(clientSide = "tamaized.melongolem.ClientProxy", serverSide = "tamaized.melongolem.ServerProxy")
	public static IModProxy proxy;
	public static SimpleNetworkWrapper network;
	private static int entityID;
	public Logger logger;

	public static String getVersion() {
		return version;
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e) {
		e.getRegistry().register(assign(new ItemMelonStick(), "melonstick"));
	}

	private static void registerEntity(String name, Class<? extends Entity> entityClass, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggPrimary, int eggSecondary) {
		ResourceLocation entityName = new ResourceLocation(MODID, name);
		EntityRegistry.registerModEntity(entityName, entityClass, entityName.getNamespace() + "." + entityName.getPath(), entityID++, instance, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimary, eggSecondary);
	}

	private static void registerEntity(String name, Class<? extends Entity> entityClass, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		ResourceLocation entityName = new ResourceLocation(MODID, name);
		EntityRegistry.registerModEntity(entityName, entityClass, entityName.getNamespace() + "." + entityName.getPath(), entityID++, instance, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		registerModel(melonStick, 0, "");
	}

	private static Item assign(Item item, String name) {
		return item

				.setRegistryName(MODID, name)

				.setTranslationKey(MODID + "." + name);
	}

	private static void registerModel(Item item, int meta, String path) {
		if (item.getRegistryName() == null)
			return;
		ModelLoader.setCustomModelResourceLocation(

				item,

				meta,

				new ModelResourceLocation(

						new ResourceLocation(item.getRegistryName().getNamespace(), path + item.getRegistryName().getPath()),

						"inventory"

				)

		);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = LogManager.getLogger(MODID);

		CapabilityManager.INSTANCE.register(ITinyGolemCapability.class, new TinyGolemCapabilityStorage(), TinyGolemCapabilityHandler::new);

		NetworkMessages.register(network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID));

		registerEntity("melon_golem", EntityMelonGolem.class, 128, 1, true, 0xFF00, 0x0);
		registerEntity("melon_slice", EntityMelonSlice.class, 128, 1, true);
		registerEntity("tiny_melon_golem", EntityTinyMelonGolem.class, 128, 1, true);

		proxy.preinit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();

		MelonConfig.setupStabby();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}

}

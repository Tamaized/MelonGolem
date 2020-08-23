package tamaized.melongolem;


import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tamaized.melongolem.client.MelonConfigScreen;
import tamaized.melongolem.common.EntityGlisteringMelonGolem;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.common.EntityMelonSlice;
import tamaized.melongolem.common.EntityTinyMelonGolem;
import tamaized.melongolem.common.ItemMelonStick;
import tamaized.melongolem.common.capability.ITinyGolemCapability;
import tamaized.melongolem.common.capability.TinyGolemCapabilityHandler;
import tamaized.melongolem.common.capability.TinyGolemCapabilityStorage;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.network.NetworkMessages;
import tamaized.melongolem.network.client.ClientPacketHandlerSpawnNonLivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Mod(MelonMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MelonMod {

	public static final String MODID = "melongolem";

	public static final IModProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

	public static MelonConfig config;
	public static MelonConfig.Client configClient;

	public static final SimpleChannel network = NetworkRegistry.ChannelBuilder.
			named(new ResourceLocation(MODID, MODID)).
			clientAcceptedVersions(s -> true).
			serverAcceptedVersions(s -> true).
			networkProtocolVersion(() -> "1").
			simpleChannel();

	public static final Logger logger = LogManager.getLogger(MODID);

	private static final Map<EntityType<? extends LivingEntity>, Supplier<AttributeModifierMap.MutableAttribute>> attributes = new HashMap<>();

	@ObjectHolder(MelonMod.MODID + ":melonstick")
	public static final Item melonStick = Items.AIR;

	@ObjectHolder(MelonMod.MODID + ":glisteringmelonblock")
	public static final Block glisteringMelonBlock = Blocks.AIR;

	@ObjectHolder(MelonMod.MODID + ":entitymelongolem")
	public static final EntityType<? extends GolemEntity> entityTypeMelonGolem = assign(EntityMelonGolem.class, 0.7F, 1.9F, 128, 1, true, EntityClassification.CREATURE, EntityMelonGolem::registerAttributes);

	@ObjectHolder(MelonMod.MODID + ":entityglisteringmelongolem")
	public static final EntityType<? extends GolemEntity> entityTypeGlisteringMelonGolem = assign(EntityGlisteringMelonGolem.class, 0.7F, 1.9F, 128, 1, true, EntityClassification.CREATURE, EntityMelonGolem::registerAttributes);

	@ObjectHolder(MelonMod.MODID + ":entitymelonslice")
	public static final EntityType<? extends EntityMelonSlice> entityTypeMelonSlice = getNull();

	@ObjectHolder(MelonMod.MODID + ":entitytinymelongolem")
	public static final EntityType<? extends TameableEntity> entityTypeTinyMelonGolem = getNull();

	public static final ImmutableSet<Item> SIGNS = ImmutableSet.of(Items.ACACIA_SIGN, Items.BIRCH_SIGN, Items.DARK_OAK_SIGN, Items.JUNGLE_SIGN, Items.JUNGLE_SIGN, Items.OAK_SIGN, Items.SPRUCE_SIGN);

	public MelonMod() {
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
		try {
			Field bitchIDoWhatIWant = ModInfo.class.getDeclaredField("config");
			bitchIDoWhatIWant.setAccessible(true);
			ModList.get().getMods().replaceAll(modInfo -> {
				if (modInfo.getModId().equalsIgnoreCase(MODID))
					try {
						IConfigurable config = (IConfigurable) bitchIDoWhatIWant.get(modInfo);
						return new ModInfo(modInfo.getOwningFile(), config) {
							@Override
							public boolean hasConfigUI() {
								return true;
							}
						};
					} catch (Throwable e) {
						e.printStackTrace();
					}
				return modInfo;
			});
		} catch (Throwable e) {
			e.printStackTrace(); // Catch all, dont crash the game just ignore this 'feature'
		}
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> MelonConfigScreen::new);
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> e) {
		e.getRegistry().registerAll(

				entityTypeMelonGolem,

				assign(EntityMelonSlice.class, 0.25F, 0.25F, 128, 1, true, EntityClassification.MISC),

				assign(EntityTinyMelonGolem.class, 0.175F, 0.475F, 128, 1, true, EntityClassification.CREATURE, EntityMelonGolem::registerAttributes),

				entityTypeGlisteringMelonGolem

		);
		attributes.forEach((type, attribute) -> GlobalEntityTypeAttributes.put(type, attribute.get().create()));
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent e) {
		ClientProxy.registerRenders();
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e) {
		e.getRegistry().registerAll(

				assign(new Block(Block.Properties.create(Material.GOURD, MaterialColor.LIME).hardnessAndResistance(1.0F).sound(SoundType.WOOD).setLightLevel(state -> 4)), "glisteringmelonblock")

		);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e) {
		e.getRegistry().registerAll(

				assign(new ItemMelonStick(new Item.Properties().group(ItemGroup.MISC)), "melonstick"),

				assign(glisteringMelonBlock),

				assign(new SpawnEggItem(entityTypeMelonGolem, 0x00FF00, 0x000000, new Item.Properties().group(ItemGroup.MISC)), "melongolemspawnegg"),

				assign(new SpawnEggItem(entityTypeGlisteringMelonGolem, 0xAAFF00, 0xFFCC00, new Item.Properties().group(ItemGroup.MISC)), "glisteringmelongolemspawnegg")

		);
	}

	private static Block assign(Block block, String name) {
		return block

				.setRegistryName(MODID, name);
	}

	private static BlockItem assign(Block block) {
		return (BlockItem) new BlockItem(block,

				new Item.Properties().setNoRepair().group(ItemGroup.MISC)

		)

				.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
	}

	private static Item assign(Item item, String name) {
		return item

				.setRegistryName(MODID, name);
	}

	private static <T extends LivingEntity> EntityType<T> assign(Class<T> entity, float w, float h, int range, int freq, boolean updates, EntityClassification classification, Supplier<AttributeModifierMap.MutableAttribute> attributes) {
		EntityType<T> type = assign(entity, w, h, range, freq, updates, classification);
		MelonMod.attributes.put(type, attributes);
		return type;
	}

	private static <T extends Entity> EntityType<T> assign(Class<T> entity, float w, float h, int range, int freq, boolean updates, EntityClassification classification) {
		final String name = entity.getSimpleName().toLowerCase();
		EntityType<T> type = EntityType.Builder.<T>create((et, world) -> {
			try {
				return entity.getConstructor(World.class).newInstance(world);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			return null;
		}, classification).
				setTrackingRange(range).
				setUpdateInterval(freq).
				setShouldReceiveVelocityUpdates(updates).
				size(w, h).
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

	public static void spawnNonLivingEntity(World world, Entity entity) {
		world.addEntity(entity);
		MelonMod.network.send(

				PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunk(entity.chunkCoordX, entity.chunkCoordZ)),

				new ClientPacketHandlerSpawnNonLivingEntity(entity)

		);
	}

}

package tamaized.melongolem;

import com.google.common.collect.ImmutableSet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fmlclient.ConfigGuiHandler;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
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
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.network.NetworkMessages;

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

	private static final Map<EntityType<? extends LivingEntity>, Supplier<AttributeSupplier.Builder>> attributes = new HashMap<>();

	@ObjectHolder(MelonMod.MODID + ":melonstick")
	public static final Item melonStick = Items.AIR;

	@ObjectHolder(MelonMod.MODID + ":glisteringmelonblock")
	public static final Block glisteringMelonBlock = Blocks.AIR;

	@ObjectHolder(MelonMod.MODID + ":entitymelongolem")
	public static final EntityType<? extends AbstractGolem> entityTypeMelonGolem = assign(EntityMelonGolem.class, 0.7F, 1.9F, 128, 1, true, MobCategory.CREATURE, EntityMelonGolem::_registerAttributes);

	@ObjectHolder(MelonMod.MODID + ":entityglisteringmelongolem")
	public static final EntityType<? extends AbstractGolem> entityTypeGlisteringMelonGolem = assign(EntityGlisteringMelonGolem.class, 0.7F, 1.9F, 128, 1, true, MobCategory.CREATURE, EntityMelonGolem::_registerAttributes);

	@ObjectHolder(MelonMod.MODID + ":entitymelonslice")
	public static final EntityType<? extends EntityMelonSlice> entityTypeMelonSlice = getNull();

	@ObjectHolder(MelonMod.MODID + ":entitytinymelongolem")
	public static final EntityType<? extends TamableAnimal> entityTypeTinyMelonGolem = getNull();

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
		/*try { FIXME: Okay, so I had to comment this all out because this method doesn't exist, but it seems as though all that is necessary is below
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
		}*/
		ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory(MelonConfigScreen::new));
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> e) {
		e.getRegistry().registerAll(

				entityTypeMelonGolem,

				assign(EntityMelonSlice.class, 0.25F, 0.25F, 128, 1, true, MobCategory.MISC),

				assign(EntityTinyMelonGolem.class, 0.175F, 0.475F, 128, 1, true, MobCategory.CREATURE, EntityMelonGolem::_registerAttributes),

				entityTypeGlisteringMelonGolem

		);
	}

	public static void registerAttributes(EntityAttributeCreationEvent event) {
		attributes.forEach((type, attribute) -> event.put(type, attribute.get().build()));
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent e) {
		ClientProxy.registerRenders();
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e) {
		e.getRegistry().registerAll(

				assign(new Block(Block.Properties.of(Material.VEGETABLE, MaterialColor.COLOR_LIGHT_GREEN).strength(1.0F).sound(SoundType.WOOD).lightLevel(state -> 4)), "glisteringmelonblock")

		);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e) {
		e.getRegistry().registerAll(

				assign(new ItemMelonStick(new Item.Properties().tab(CreativeModeTab.TAB_MISC)), "melonstick"),

				assign(glisteringMelonBlock),

				assign(new ForgeSpawnEggItem(() -> entityTypeMelonGolem, 0x00FF00, 0x000000, new Item.Properties().tab(CreativeModeTab.TAB_MISC)), "melongolemspawnegg"),

				assign(new ForgeSpawnEggItem(() -> entityTypeGlisteringMelonGolem, 0xAAFF00, 0xFFCC00, new Item.Properties().tab(CreativeModeTab.TAB_MISC)), "glisteringmelongolemspawnegg")

		);
	}

	private static Block assign(Block block, String name) {
		return block

				.setRegistryName(MODID, name);
	}

	private static BlockItem assign(Block block) {
		return (BlockItem) new BlockItem(block,

				new Item.Properties().setNoRepair().tab(CreativeModeTab.TAB_MISC)

		)

				.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
	}

	private static Item assign(Item item, String name) {
		return item

				.setRegistryName(MODID, name);
	}

	private static <T extends LivingEntity> EntityType<T> assign(Class<T> entity, float w, float h, int range, int freq, boolean updates, MobCategory classification, Supplier<AttributeSupplier.Builder> attributes) {
		EntityType<T> type = assign(entity, w, h, range, freq, updates, classification);
		MelonMod.attributes.put(type, attributes);
		return type;
	}

	private static <T extends Entity> EntityType<T> assign(Class<T> entity, float w, float h, int range, int freq, boolean updates, MobCategory classification) {
		final String name = entity.getSimpleName().toLowerCase();
		EntityType<T> type = EntityType.Builder.<T>of((et, world) -> {
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
		type.setRegistryName(MODID, name);
		return type;
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
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

	public static void spawnNonLivingEntity(Level world, Entity entity) {
		world.addFreshEntity(entity);
		MelonMod.network.send(

				PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunk(entity.chunkPosition().x, entity.chunkPosition().z)),

				new ClientboundAddEntityPacket(entity)

		);
	}

}

package tamaized.melongolem;


import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Random;
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

	@ObjectHolder(MelonMod.MODID + ":glisteringmelonblock")
	public static final Block glisteringMelonBlock = Blocks.AIR;

	public static final EntityType entityTypeMelonGolem = assign(EntityMelonGolem.class, 128, 1, true);

	public static final EntityType entityTypeGlisteringMelonGolem = assign(EntityGlisteringMelonGolem.class, 128, 1, true);

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

				assign(EntityTinyMelonGolem.class, 128, 1, true),

				entityTypeGlisteringMelonGolem

		);
	}

	@SubscribeEvent
	public static void registerRenders(FMLClientSetupEvent e) {
		RenderingRegistry.registerEntityRenderingHandler(EntityMelonGolem.class, RenderMelonGolem.Factory::normal);
		RenderingRegistry.registerEntityRenderingHandler(EntityMelonSlice.class, RenderMelonSlice::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTinyMelonGolem.class, RenderMelonGolem.Factory::tiny);
		RenderingRegistry.registerEntityRenderingHandler(EntityGlisteringMelonGolem.class, RenderMelonGolem.Factory::glister);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e) {
		e.getRegistry().registerAll(

				assign(new Block(Block.Properties.create(Material.GOURD, MaterialColor.LIME).hardnessAndResistance(1.0F).sound(SoundType.WOOD).lightValue(4)) {
					@Nonnull
					@Override
					public IItemProvider getItemDropped(IBlockState p_199769_1_, World p_199769_2_, BlockPos p_199769_3_, int p_199769_4_) {
						return Items.GLISTERING_MELON_SLICE;
					}

					@Override
					@Deprecated
					@SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
					public int quantityDropped(IBlockState p_196264_1_, Random p_196264_2_) {
						return 3 + p_196264_2_.nextInt(5);
					}

					@Override
					@SuppressWarnings("deprecation")
					public int getItemsToDropCount(@Nonnull IBlockState p_196251_1_, int p_196251_2_, World p_196251_3_, BlockPos p_196251_4_, @Nonnull Random p_196251_5_) {
						return Math.min(9, this.quantityDropped(p_196251_1_, p_196251_5_) + p_196251_5_.nextInt(1 + p_196251_2_));
					}

					@Nonnull
					@Override
					public BlockRenderLayer getRenderLayer() {
						return BlockRenderLayer.CUTOUT;
					}
				}, "glisteringmelonblock")

		);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e) {
		e.getRegistry().registerAll(

				assign(new ItemMelonStick(new Item.Properties().group(ItemGroup.MISC)), "melonstick"),

				assign(glisteringMelonBlock),

				assign(new ItemSpawnEgg(entityTypeMelonGolem, 0xFF00, 0x0, new Item.Properties().group(ItemGroup.MISC)), "melongolemspawnegg"),

				assign(new ItemSpawnEgg(entityTypeGlisteringMelonGolem, 0xAAFF00, 0xFFCC00, new Item.Properties().group(ItemGroup.MISC)), "glisteringmelongolemspawnegg")

		);
	}

	private static Block assign(Block block, String name) {
		return block

				.setRegistryName(MODID, name);
	}

	private static ItemBlock assign(Block block) {
		return (ItemBlock) new ItemBlock(block,

				new Item.Properties().setNoRepair().group(ItemGroup.MISC)

		)

				.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
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

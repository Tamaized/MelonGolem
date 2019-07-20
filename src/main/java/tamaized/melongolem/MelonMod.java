package tamaized.melongolem;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
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

	public static final EntityType<? extends GolemEntity> entityTypeMelonGolem = assign(EntityMelonGolem.class, 0.7F, 1.9F, 128, 1, true, EntityClassification.CREATURE);

	public static final EntityType<? extends GolemEntity> entityTypeGlisteringMelonGolem = assign(EntityGlisteringMelonGolem.class, 0.7F, 1.9F, 128, 1, true, EntityClassification.CREATURE);

	@ObjectHolder(MelonMod.MODID + ":entitymelonslice")
	public static final EntityType<? extends ThrowableEntity> entityTypeMelonSlice = getNull();

	@ObjectHolder(MelonMod.MODID + ":entitytinymelongolem")
	public static final EntityType<? extends TameableEntity> entityTypeTinyMelonGolem = getNull();

	public MelonMod() {
		DonatorHandler.start();
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> e) {
		e.getRegistry().registerAll(

				entityTypeMelonGolem,

				assign(EntityMelonSlice.class, 0.25F, 0.25F, 128, 1, true, EntityClassification.MISC),

				assign(EntityTinyMelonGolem.class, 0.175F, 0.475F, 128, 1, true, EntityClassification.CREATURE),

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
					/*@Nonnull
					@Override
					public IItemProvider getItemDropped(BlockState p_199769_1_, World p_199769_2_, BlockPos p_199769_3_, int p_199769_4_) {
						return Items.GLISTERING_MELON_SLICE;
					}

					@Override
					@Deprecated
					@SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
					public int quantityDropped(BlockState p_196264_1_, Random p_196264_2_) {
						return 3 + p_196264_2_.nextInt(5);
					}

					@Override
					@SuppressWarnings("deprecation")
					public int getItemsToDropCount(@Nonnull BlockState p_196251_1_, int p_196251_2_, World p_196251_3_, BlockPos p_196251_4_, @Nonnull Random p_196251_5_) {
						return Math.min(9, this.quantityDropped(p_196251_1_, p_196251_5_) + p_196251_5_.nextInt(1 + p_196251_2_));
					}*/

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

				assign(new SpawnEggItem(entityTypeMelonGolem, 0xFF00, 0x0, new Item.Properties().group(ItemGroup.MISC)), "melongolemspawnegg"),

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

}

package tamaized.melongolem;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tamaized.melongolem.client.MelonConfigScreen;
import tamaized.melongolem.registry.*;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.network.NetworkMessages;
import tamaized.regutil.RegUtil;

@Mod(MelonMod.MODID)
public class MelonMod {

	public static final String MODID = "melongolem";

	public static MelonConfig config;
	public static MelonConfig.Client configClient;

	public static final Logger logger = LogManager.getLogger(MODID);

	public MelonMod(IEventBus busMod) {
		MelonConfig.init(busMod);
		DonatorHandler.start();

		{
			final Pair<MelonConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(MelonConfig::new);
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, specPair.getRight());
			config = specPair.getLeft();
		}
		{
			final Pair<MelonConfig.Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(MelonConfig.Client::new);
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
			configClient = specPair.getLeft();
		}
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(MelonConfigScreen::new));

		RegUtil.setup(MODID, busMod,
				ModBlocks::new,
				ModItems::new,
				ModEntities::new,
				ModCreativeTabs::new,
				ModSounds::new
				);

		NetworkMessages.register(busMod);
	}

}

package tamaized.melongolem;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tamaized.beanification.BeanContext;
import tamaized.melongolem.client.ClientInitiator;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.regutil.RegUtil;

@Mod(MelonMod.MODID)
public class MelonMod {

	public static final String MODID = "melongolem";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	static {
		BeanContext.init();
	}

	public MelonMod(IEventBus busMod) {
		BeanContext.enableMainModClassInjections(this);

		if (FMLEnvironment.dist == Dist.CLIENT)
			ClientInitiator.call(busMod);

		DonatorHandler.start();

//		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(MelonConfigScreen::new));

		RegUtil.setup(MODID, busMod);
	}

}

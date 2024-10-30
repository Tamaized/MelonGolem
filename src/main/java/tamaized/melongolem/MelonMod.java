package tamaized.melongolem;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tamaized.beanification.BeanContext;
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
		RegUtil.setup(MODID, busMod);
	}

}

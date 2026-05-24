package tamaized.melongolem;

import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tamaized.beanification.BeanContext;
import tamaized.datagenutil.DataGenUtilConstants;
import tamaized.regutil.RegUtil;

@Mod(MelonMod.MODID)
public class MelonMod {

	public static final String MODID = "melongolem";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	static {
		BeanContext.configure()
			.scanSettings().addAdditionalComponentScanModuleName(RegUtil.MODULE_NAME)
			.scanSettings().addAdditionalComponentScanModuleName(DataGenUtilConstants.MODULE_NAME);
		BeanContext.init(MODID);
	}

}

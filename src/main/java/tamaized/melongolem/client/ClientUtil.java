package tamaized.melongolem.client;

import net.minecraft.client.Minecraft;
import tamaized.melongolem.ISignHolder;

public class ClientUtil {

	public static void openGolemSignScreen(ISignHolder golem) {
		Minecraft.getInstance().setScreen(new GuiEditGolemSign(golem));
	}

}

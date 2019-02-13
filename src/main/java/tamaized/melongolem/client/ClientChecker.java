package tamaized.melongolem.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import tamaized.melongolem.MelonConfig;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.network.server.ServerPacketHandlerDonatorSettings;

@Mod.EventBusSubscriber(modid = MelonMod.MODID, value = Side.CLIENT)
public class ClientChecker {

	@SubscribeEvent
	public static void onUpdate(TickEvent.ClientTickEvent event) {
		if (Minecraft.getMinecraft().world == null) {
			MelonConfig.dirty = true;
			return;
		}
		if (event.phase == TickEvent.Phase.START) {
			if (MelonConfig.dirty && DonatorHandler.donators.contains(Minecraft.getMinecraft().player.getUniqueID())) {
				MelonMod.network.sendToServer(new ServerPacketHandlerDonatorSettings.Packet(new DonatorHandler.DonatorSettings(MelonConfig.donatorSettings.enable, MelonConfig.donatorSettings.colorint)));
				MelonConfig.dirty = false;
			}
		}
	}

}

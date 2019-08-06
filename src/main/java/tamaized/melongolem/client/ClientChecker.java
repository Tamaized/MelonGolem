package tamaized.melongolem.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tamaized.melongolem.MelonConfig;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.network.server.ServerPacketHandlerDonatorSettings;

@Mod.EventBusSubscriber(modid = MelonMod.MODID, value = Dist.CLIENT)
public class ClientChecker {

	@SubscribeEvent
	public static void onUpdate(TickEvent.ClientTickEvent event) {
		if (Minecraft.getInstance().world == null) {
			MelonConfig.dirty = true;
			return;
		}
		if (event.phase == TickEvent.Phase.START) {
			if (MelonConfig.dirty && DonatorHandler.donators.contains(Minecraft.getInstance().player.getUniqueID())) {
				MelonMod.network.sendToServer(new ServerPacketHandlerDonatorSettings(new DonatorHandler.DonatorSettings(MelonMod.config.DONATOR_SETTINGS.enable.get(), MelonMod.config.DONATOR_SETTINGS.colorint)));
				MelonConfig.dirty = false;
			}
		}
	}

}

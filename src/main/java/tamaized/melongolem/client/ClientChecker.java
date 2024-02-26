package tamaized.melongolem.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import tamaized.melongolem.MelonConfig;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.network.server.ServerPacketHandlerDonatorSettings;

@Mod.EventBusSubscriber(modid = MelonMod.MODID, value = Dist.CLIENT)
public class ClientChecker {

	@SubscribeEvent
	public static void onUpdate(TickEvent.ClientTickEvent event) {
		if (Minecraft.getInstance().level == null) {
			MelonConfig.dirty = true;
			return;
		}
		if (event.phase == TickEvent.Phase.START) {
			if (MelonConfig.dirty && DonatorHandler.donators.contains(Minecraft.getInstance().player.getUUID())) {
				PacketDistributor.SERVER.noArg().send(new ServerPacketHandlerDonatorSettings(new DonatorHandler.DonatorSettings(MelonMod.config.DONATOR_SETTINGS.enable.get(), MelonMod.config.DONATOR_SETTINGS.colorint)));
				MelonConfig.dirty = false;
			}
		}
	}

}

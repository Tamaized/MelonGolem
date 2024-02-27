package tamaized.melongolem.client;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import tamaized.melongolem.MelonConfig;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.network.server.ServerPacketDonatorSettings;

public class ClientListener {

	static void init(IEventBus modBus) {
		NeoForge.EVENT_BUS.addListener(TickEvent.ClientTickEvent.class, event -> {
			if (Minecraft.getInstance().level == null) {
				MelonConfig.dirty = true;
				return;
			}
			if (event.phase == TickEvent.Phase.START) {
				if (MelonConfig.dirty && DonatorHandler.donators.contains(Minecraft.getInstance().player.getUUID())) {
					PacketDistributor.SERVER.noArg().send(new ServerPacketDonatorSettings(new DonatorHandler.DonatorSettings(MelonMod.config.DONATOR_SETTINGS.enable.get(), MelonMod.config.DONATOR_SETTINGS.colorint)));
					MelonConfig.dirty = false;
				}
			}
		});
	}

}

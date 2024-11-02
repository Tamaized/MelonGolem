package tamaized.melongolem.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.beanification.PostConstruct;
import tamaized.melongolem.config.client.DonatorSettings;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.network.server.ServerPacketDonatorSettings;

@Component(dist = Dist.CLIENT)
public class ClientListener {

	@Autowired
	private DonatorHandler donatorHandler;

	@Autowired
	private DonatorSettings donatorSettingsConfig;

	@PostConstruct
	private void init(IEventBus modBus) {
		NeoForge.EVENT_BUS.addListener(ClientTickEvent.Pre.class, event -> {
			if (Minecraft.getInstance().level == null) {
				donatorSettingsConfig.markDirty();
				return;
			}

			if (donatorSettingsConfig.isDirty() && Minecraft.getInstance().player != null && donatorHandler.isDonator(Minecraft.getInstance().player.getUUID())) {
				PacketDistributor.sendToServer(new ServerPacketDonatorSettings(new DonatorHandler.Settings(donatorSettingsConfig.enable.get(), donatorSettingsConfig.color.get())));
				donatorSettingsConfig.unmarkDirty();
			}
		});
	}

}

package tamaized.melongolem.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import tamaized.beanification.Component;
import tamaized.beanification.PostConstruct;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.network.client.ClientPacketMelonAmbientSound;
import tamaized.melongolem.network.client.ClientPacketSendParticles;
import tamaized.melongolem.network.server.ServerPacketDonatorSettings;
import tamaized.melongolem.network.server.ServerPacketMelonSign;

@Component
public class NetworkMessages {

	@PostConstruct
	private void setup(IEventBus busMod) {
		busMod.addListener(RegisterPayloadHandlersEvent.class, event -> {
			final PayloadRegistrar registrar = event.registrar(MelonMod.MODID).versioned("1").optional();

			registrar.playToServer(ServerPacketMelonSign.ID, ServerPacketMelonSign.CODEC, ServerPacketMelonSign::handle);
			registrar.playToServer(ServerPacketDonatorSettings.ID, ServerPacketDonatorSettings.CODEC, ServerPacketDonatorSettings::handle);

			registrar.playToClient(ClientPacketMelonAmbientSound.ID, ClientPacketMelonAmbientSound.CODEC, ClientPacketMelonAmbientSound::handle);
			registrar.playToClient(ClientPacketSendParticles.ID, ClientPacketSendParticles.CODEC, ClientPacketSendParticles::handle);
		});
	}

}

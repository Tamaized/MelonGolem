package tamaized.melongolem.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.network.client.ClientPacketMelonAmbientSound;
import tamaized.melongolem.network.client.ClientPacketSendParticles;
import tamaized.melongolem.network.server.ServerPacketDonatorSettings;
import tamaized.melongolem.network.server.ServerPacketMelonSign;

public class NetworkMessages {

	public static void register(IEventBus busMod) {
		busMod.addListener(RegisterPayloadHandlerEvent.class, event -> {
			IPayloadRegistrar network = event.registrar(MelonMod.MODID)
					.versioned("1")
					.optional();

			final IPayloadRegistrar registrar = event.registrar(MelonMod.MODID).versioned("1").optional();

			registrar.play(ServerPacketMelonSign.ID, ServerPacketMelonSign::new, payload -> payload.server(ServerPacketMelonSign::handle));
			registrar.play(ServerPacketDonatorSettings.ID, ServerPacketDonatorSettings::new, payload -> payload.server(ServerPacketDonatorSettings::handle));

			registrar.play(ClientPacketMelonAmbientSound.ID, ClientPacketMelonAmbientSound::new, payload -> payload.client(ClientPacketMelonAmbientSound::handle));
			registrar.play(ClientPacketSendParticles.ID, ClientPacketSendParticles::new, payload -> payload.client(ClientPacketSendParticles::handle));
		});
	}

}

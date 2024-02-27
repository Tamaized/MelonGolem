package tamaized.melongolem.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.network.client.ClientPacketHandlerMelonAmbientSound;
import tamaized.melongolem.network.client.ClientPacketHandlerParticle;
import tamaized.melongolem.network.server.ServerPacketHandlerDonatorSettings;
import tamaized.melongolem.network.server.ServerPacketHandlerMelonSign;

public class NetworkMessages {

	public static void register(IEventBus busMod) {
		busMod.addListener(RegisterPayloadHandlerEvent.class, event -> {
			IPayloadRegistrar network = event.registrar(MelonMod.MODID)
					.versioned("1")
					.optional();

			final IPayloadRegistrar registrar = event.registrar(MelonMod.MODID).versioned("1").optional();

			registrar.play(ServerPacketHandlerMelonSign.ID, ServerPacketHandlerMelonSign::new, payload -> payload.server(ServerPacketHandlerMelonSign::handle));
			registrar.play(ServerPacketHandlerDonatorSettings.ID, ServerPacketHandlerDonatorSettings::new, payload -> payload.server(ServerPacketHandlerDonatorSettings::handle));

			registrar.play(ClientPacketHandlerMelonAmbientSound.ID, ClientPacketHandlerMelonAmbientSound::new, payload -> payload.client(ClientPacketHandlerMelonAmbientSound::handle));
			registrar.play(ClientPacketHandlerParticle.ID, ClientPacketHandlerParticle::new, payload -> payload.client(ClientPacketHandlerParticle::handle));
		});
	}

}

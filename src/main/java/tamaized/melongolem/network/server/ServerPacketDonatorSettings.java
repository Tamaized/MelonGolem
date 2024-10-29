package tamaized.melongolem.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.network.DonatorHandler;

public record ServerPacketDonatorSettings(DonatorHandler.DonatorSettings settings) implements CustomPacketPayload {

	public static final Type<ServerPacketDonatorSettings> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(MelonMod.MODID, "c2s_donator_settings"));

	public static final StreamCodec<FriendlyByteBuf, ServerPacketDonatorSettings> CODEC = StreamCodec.ofMember(ServerPacketDonatorSettings::write, ServerPacketDonatorSettings::new);

	public ServerPacketDonatorSettings(FriendlyByteBuf buf) {
		this(new DonatorHandler.DonatorSettings(buf.readBoolean(), buf.readInt()));
	}

	public void write(FriendlyByteBuf packet) {
		packet.writeBoolean(settings().enabled);
		packet.writeInt(settings().color);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public static void handle(final ServerPacketDonatorSettings packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (DonatorHandler.donators.contains(context.player().getUUID()))
				DonatorHandler.settings.put(context.player().getUUID(), packet.settings());
		});
	}
}

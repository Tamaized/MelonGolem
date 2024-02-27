package tamaized.melongolem.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.network.DonatorHandler;

public record ServerPacketDonatorSettings(DonatorHandler.DonatorSettings settings) implements CustomPacketPayload {

	public static final ResourceLocation ID = new ResourceLocation(MelonMod.MODID, "c2s_donator_settings");

	public ServerPacketDonatorSettings(FriendlyByteBuf buf) {
		this(new DonatorHandler.DonatorSettings(buf.readBoolean(), buf.readInt()));
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public static void handle(final ServerPacketDonatorSettings packet, PlayPayloadContext context) {
		context.workHandler().execute(() ->
				context.player().ifPresent(player -> {
					if (DonatorHandler.donators.contains(player.getUUID()))
						DonatorHandler.settings.put(player.getUUID(), packet.settings());
				}));
	}

	@Override
	public void write(FriendlyByteBuf packet) {
		packet.writeBoolean(settings().enabled);
		packet.writeInt(settings().color);
	}
}

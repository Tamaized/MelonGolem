package tamaized.melongolem.network.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import tamaized.melongolem.network.DonatorHandler;
import tamaized.melongolem.network.NetworkMessages;

public class ServerPacketHandlerDonatorSettings implements NetworkMessages.IMessage<ServerPacketHandlerDonatorSettings> {

	private DonatorHandler.DonatorSettings settings;

	public ServerPacketHandlerDonatorSettings(DonatorHandler.DonatorSettings settings) {
		this.settings = settings;
	}

	@Override
	public void handle(EntityPlayer player) {
		if (DonatorHandler.donators.contains(player.getUniqueID()))
			DonatorHandler.settings.put(player.getUniqueID(), settings);
	}

	@Override
	public void toBytes(PacketBuffer packet) {
		packet.writeBoolean(settings.enabled);
		packet.writeInt(settings.color);
	}

	@Override
	public ServerPacketHandlerDonatorSettings fromBytes(PacketBuffer packet) {
		settings = new DonatorHandler.DonatorSettings(packet.readBoolean(), packet.readInt());
		return this;
	}
}

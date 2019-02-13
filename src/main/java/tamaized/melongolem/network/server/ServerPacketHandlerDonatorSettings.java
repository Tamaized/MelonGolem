package tamaized.melongolem.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tamaized.melongolem.network.DonatorHandler;

public class ServerPacketHandlerDonatorSettings implements IMessageHandler<ServerPacketHandlerDonatorSettings.Packet, IMessage> {

	private static void processPacket(Packet message, EntityPlayerMP player, World world) {
		if (DonatorHandler.donators.contains(player.getUniqueID()))
			DonatorHandler.settings.put(player.getUniqueID(), message.settings);
	}

	@Override
	public IMessage onMessage(Packet message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().player;
		MinecraftServer server = player.getServer();
		if (server != null)
			server.addScheduledTask(() -> processPacket(message, player, player.world));
		return null;
	}

	public static class Packet implements IMessage {

		private DonatorHandler.DonatorSettings settings;

		@SuppressWarnings("unused")
		public Packet() {

		}

		public Packet(DonatorHandler.DonatorSettings settings) {
			this.settings = settings;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			settings = new DonatorHandler.DonatorSettings(buf.readBoolean(), buf.readInt());
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeBoolean(settings.enabled);
			buf.writeInt(settings.color);
		}
	}
}

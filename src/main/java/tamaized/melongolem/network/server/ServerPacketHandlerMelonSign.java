package tamaized.melongolem.network.server;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.network.NetworkMessages;

public class ServerPacketHandlerMelonSign implements NetworkMessages.IMessage<ServerPacketHandlerMelonSign> {

	private int id;
	private String[] lines;

	public ServerPacketHandlerMelonSign(ISignHolder golem) {
		id = golem.networkID();
		lines = new String[]{golem.getSignText(0).getString(), golem.getSignText(1).getString(), golem.getSignText(2).getString(), golem.getSignText(3).getString()};
	}

	@Override
	public void handle(Player player) {
		Entity entity = player.level.getEntity(id);
		if (entity instanceof EntityMelonGolem && entity.distanceTo(player) <= 6)
			for (int i = 0; i < lines.length; ++i) {
				String text = ChatFormatting.stripFormatting(lines[i]);
				((EntityMelonGolem) entity).setSignText(i, new TextComponent(text == null ? "" : text));
			}
	}

	@Override
	public void toBytes(FriendlyByteBuf packet) {
		packet.writeInt(id);
		for (int i = 0; i < 4; ++i)
			packet.writeUtf(lines[i]);
	}

	@Override
	public ServerPacketHandlerMelonSign fromBytes(FriendlyByteBuf packet) {
		id = packet.readInt();
		lines = new String[4];
		for (int i = 0; i < 4; ++i) {
			this.lines[i] = packet.readUtf(Short.MAX_VALUE);
		}
		return this;
	}

}

package tamaized.melongolem.network.server;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.network.NetworkMessages;

public class ServerPacketHandlerMelonSign implements NetworkMessages.IMessage<ServerPacketHandlerMelonSign> {

	private int id;
	private String[] lines;

	public ServerPacketHandlerMelonSign(IModProxy.ISignHolder golem) {
		id = golem.getEntityId();
		lines = new String[]{golem.getSignText(0).getString(), golem.getSignText(1).getString(), golem.getSignText(2).getString(), golem.getSignText(3).getString()};
	}

	@Override
	public void handle(PlayerEntity player) {
		Entity entity = player.world.getEntityByID(id);
		if (entity instanceof EntityMelonGolem && entity.getDistance(player) <= 6)
			for (int i = 0; i < lines.length; ++i) {
				String text = TextFormatting.getTextWithoutFormattingCodes(lines[i]);
				((EntityMelonGolem) entity).setSignText(i, new StringTextComponent(text == null ? "" : text));
			}
	}

	@Override
	public void toBytes(PacketBuffer packet) {
		packet.writeInt(id);
		for (int i = 0; i < 4; ++i)
			packet.writeString(lines[i]);
	}

	@Override
	public ServerPacketHandlerMelonSign fromBytes(PacketBuffer packet) {
		id = packet.readInt();
		lines = new String[4];
		for (int i = 0; i < 4; ++i) {
			this.lines[i] = packet.readString(Short.MAX_VALUE);
		}
		return this;
	}

}

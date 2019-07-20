package tamaized.melongolem.network.client;

import com.mojang.text2speech.Narrator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextFormatting;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.network.NetworkMessages;

public class ClientPacketHandlerMelonTTS implements NetworkMessages.IMessage<ClientPacketHandlerMelonTTS> {

	private static Narrator narrator;

	private int id;

	public ClientPacketHandlerMelonTTS(EntityMelonGolem golem) {
		id = golem.getEntityId();
	}

	@Override
	public void handle(PlayerEntity player) {
		Entity entity = player.world.getEntityByID(id);
		if (entity instanceof EntityMelonGolem && entity.getDistanceSq(player) <= 225) {
			if (narrator == null)
				narrator = Narrator.getNarrator();
			if (!narrator.active())
				return;
			narrator.clear();
			EntityMelonGolem golem = (EntityMelonGolem) entity;
			StringBuilder string = new StringBuilder();
			for (int i = 0; i < 4; ++i)
				string.append(TextFormatting.getTextWithoutFormattingCodes(golem.getSignText(i).getString())).append(" ");
			narrator.say(string.toString(), false);
		}
	}

	@Override
	public void toBytes(PacketBuffer packet) {
		packet.writeInt(id);
	}

	@Override
	public ClientPacketHandlerMelonTTS fromBytes(PacketBuffer packet) {
		id = packet.readInt();
		return this;
	}
}

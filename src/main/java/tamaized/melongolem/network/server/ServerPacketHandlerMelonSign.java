package tamaized.melongolem.network.server;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityMelonGolem;

public record ServerPacketHandlerMelonSign(int entityID, String[] lines) implements CustomPacketPayload {

	public static final ResourceLocation ID = new ResourceLocation(MelonMod.MODID, "edit_melon_sign");

	public ServerPacketHandlerMelonSign(ISignHolder golem) {
		this(golem.networkID(), new String[]{golem.getSignText(0).getString(), golem.getSignText(1).getString(), golem.getSignText(2).getString(), golem.getSignText(3).getString()});
	}

	public ServerPacketHandlerMelonSign(FriendlyByteBuf buf) {
		this(buf.readInt(), new String[]{ buf.readUtf(Short.MAX_VALUE), buf.readUtf(Short.MAX_VALUE), buf.readUtf(Short.MAX_VALUE), buf.readUtf(Short.MAX_VALUE)});
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public static void handle(final ServerPacketHandlerMelonSign packet, PlayPayloadContext context) {
		context.workHandler().execute(() -> context.level().flatMap(level -> context.player()).ifPresent(player -> {
			Entity entity = player.level().getEntity(packet.entityID());
			if (entity instanceof EntityMelonGolem && entity.distanceTo(player) <= 6)
				for (int i = 0; i < packet.lines.length; ++i) {
					String text = ChatFormatting.stripFormatting(packet.lines[i]);
					((EntityMelonGolem) entity).setSignText(i, Component.literal(text == null ? "" : text));
				}
		}));
	}

	@Override
	public void write(FriendlyByteBuf packet) {
		packet.writeInt(entityID);
		for (int i = 0; i < 4; ++i)
			packet.writeUtf(lines[i]);
	}
}

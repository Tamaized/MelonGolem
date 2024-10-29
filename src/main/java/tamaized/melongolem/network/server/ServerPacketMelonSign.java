package tamaized.melongolem.network.server;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.MelonMod;

public record ServerPacketMelonSign(int entityID, String[] lines) implements CustomPacketPayload {

	public static final Type<ServerPacketMelonSign> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(MelonMod.MODID, "c2s_edit_melon_sign"));

	public static final StreamCodec<FriendlyByteBuf, ServerPacketMelonSign> CODEC = StreamCodec.ofMember(ServerPacketMelonSign::write, ServerPacketMelonSign::new);

	public ServerPacketMelonSign(ISignHolder golem) {
		this(golem.networkID(), new String[]{golem.getSignText(0).getString(), golem.getSignText(1).getString(), golem.getSignText(2).getString(), golem.getSignText(3).getString()});
	}

	public ServerPacketMelonSign(FriendlyByteBuf buf) {
		this(buf.readInt(), new String[]{buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf()});
	}

	public void write(FriendlyByteBuf packet) {
		packet.writeInt(entityID);
		for (int i = 0; i < 4; ++i)
			packet.writeUtf(lines[i]);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public static void handle(final ServerPacketMelonSign packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Entity entity = context.player().level().getEntity(packet.entityID());
			if (entity instanceof ISignHolder && entity.distanceTo(context.player()) <= 6)
				for (int i = 0; i < packet.lines.length; ++i) {
					String text = ChatFormatting.stripFormatting(packet.lines[i]);
					((ISignHolder) entity).setSignText(i, Component.literal(text == null ? "" : text));
				}
		});
	}
}

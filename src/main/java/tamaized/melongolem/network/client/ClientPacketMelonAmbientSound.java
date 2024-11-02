package tamaized.melongolem.network.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tamaized.beanification.Autowired;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.client.NarratorHelper;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.config.client.ClientConfig;
import tamaized.melongolem.registry.ModSounds;

import javax.annotation.Nullable;

public record ClientPacketMelonAmbientSound(int entityID) implements CustomPacketPayload {

	public static final Type<ClientPacketMelonAmbientSound> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(MelonMod.MODID, "s2c_melon_ambient_sound"));

	public static final StreamCodec<FriendlyByteBuf, ClientPacketMelonAmbientSound> CODEC = StreamCodec.ofMember(ClientPacketMelonAmbientSound::write, ClientPacketMelonAmbientSound::new);

	@Autowired
	private static ClientConfig config;

	@Autowired
	private static ModSounds sounds;

	@Autowired(dist = Dist.CLIENT)
	private static NarratorHelper narratorHelper;

	public ClientPacketMelonAmbientSound(EntityMelonGolem golem) {
		this(golem.getId());
	}

	public ClientPacketMelonAmbientSound(FriendlyByteBuf buf) {
		this(buf.readInt());
	}

	public void write(FriendlyByteBuf packet) {
		packet.writeInt(entityID);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public static void handle(final ClientPacketMelonAmbientSound packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player().level().getEntity(packet.entityID) instanceof EntityMelonGolem golem) {
				if (golem.getHead().is(ItemTags.SIGNS)) {
					if (config.tts.get() && golem.distanceToSqr(context.player()) <= 225) {
						StringBuilder string = new StringBuilder();
						for (int i = 0; i < 4; ++i)
							string.append(ChatFormatting.stripFormatting(golem.getSignText(i).getString())).append(" ");
						narratorHelper.say(string.toString());
					}
				} else
					playAmbientSound(context.player(), golem);
			}
		});
	}

	private static void playAmbientSound(Player player, EntityMelonGolem golem) {
		SoundEvent soundevent = getAmbientSound();
		if (soundevent != null)
			golem.level().playSound(player, golem.getX(), golem.getY(), golem.getZ(), soundevent, golem.getSoundSource(), golem.getSoundVolume(), getVoicePitch(golem));
	}

	@Nullable
	private static SoundEvent getAmbientSound() {
		return config.tehnutMode.get() ? sounds.DADDY.get() : null;
	}

	private static float getVoicePitch(EntityMelonGolem golem) {
		return config.tehnutMode.get() ? golem.getPitch() + golem.level().getRandom().nextFloat() * 0.25F - 0.50F : golem.getVoicePitch();
	}

}

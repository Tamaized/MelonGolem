package tamaized.melongolem.network.client;

import com.mojang.text2speech.Narrator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.registry.ModSounds;

import javax.annotation.Nullable;

public record ClientPacketMelonAmbientSound(int entityID) implements CustomPacketPayload {

	public static final ResourceLocation ID = new ResourceLocation(MelonMod.MODID, "s2c_melon_ambient_sound");
	private static Narrator narrator;

	public ClientPacketMelonAmbientSound(EntityMelonGolem golem) {
		this(golem.getId());
	}

	public ClientPacketMelonAmbientSound(FriendlyByteBuf buf) {
		this(buf.readInt());
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public static void handle(final ClientPacketMelonAmbientSound packet, PlayPayloadContext context) {
		context.workHandler().execute(() ->
				context.player().ifPresent(player -> {
					Entity entity = player.level().getEntity(packet.entityID);
					if (entity instanceof EntityMelonGolem golem) {
						if (golem.getHead().is(ItemTags.SIGNS)) {
							if (MelonMod.configClient.tts.get() && golem.distanceToSqr(player) <= 225) {
								if (narrator == null)
									narrator = Narrator.getNarrator();
								if (!narrator.active())
									return;
								narrator.clear();
								StringBuilder string = new StringBuilder();
								for (int i = 0; i < 4; ++i)
									string.append(ChatFormatting.stripFormatting(golem.getSignText(i).getString())).append(" ");
								narrator.say(string.toString(), false);
							}
						} else
							playAmbientSound(player, golem);
					}
				}));
	}

	private static void playAmbientSound(Player player, EntityMelonGolem golem) {
		SoundEvent soundevent = getAmbientSound();
		if (soundevent != null)
			golem.level().playSound(player, golem.getX(), golem.getY(), golem.getZ(), soundevent, golem.getSoundSource(), golem.getSoundVolume(), getVoicePitch(golem));
	}

	@Nullable
	private static SoundEvent getAmbientSound() {
		return MelonMod.configClient.tehnutMode.get() ? ModSounds.DADDY.get() : null;
	}

	private static float getVoicePitch(EntityMelonGolem golem) {
		return MelonMod.configClient.tehnutMode.get() ? golem.getPitch() + golem.level().getRandom().nextFloat() * 0.25F - 0.50F : golem.getVoicePitch();
	}

	@Override
	public void write(FriendlyByteBuf packet) {
		packet.writeInt(entityID);
	}
}

package tamaized.melongolem.network.client;

import com.mojang.text2speech.Narrator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.MelonSounds;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.network.NetworkMessages;

import javax.annotation.Nullable;

public class ClientPacketHandlerMelonAmbientSound implements NetworkMessages.IMessage<ClientPacketHandlerMelonAmbientSound> {

	private static Narrator narrator;

	private int id;

	public ClientPacketHandlerMelonAmbientSound(EntityMelonGolem golem) {
		id = golem.getId();
	}

	private static void playAmbientSound(Player player, EntityMelonGolem golem) {
		SoundEvent soundevent = getAmbientSound();
		if (soundevent != null)
			golem.level.playSound(player, golem.getX(), golem.getY(), golem.getZ(), soundevent, golem.getSoundSource(), golem.getSoundVolume(), getVoicePitch(golem));
	}

	private static float getVoicePitch(EntityMelonGolem golem) {
		return MelonMod.configClient.tehnutMode.get() ? golem.getPitch() + golem.level.random.nextFloat() * 0.25F - 0.50F : golem.getVoicePitch();
	}

	@Nullable
	private static SoundEvent getAmbientSound() {
		return MelonMod.configClient.tehnutMode.get() ? MelonSounds.daddy : null;
	}

	@Override
	public void handle(Player player) {
		Entity entity = player.level.getEntity(id);
		if (entity instanceof EntityMelonGolem) {
			EntityMelonGolem golem = (EntityMelonGolem) entity;
			if (MelonMod.SIGNS.contains(golem.getHead().getItem())) {
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
	}

	@Override
	public void toBytes(FriendlyByteBuf packet) {
		packet.writeInt(id);
	}

	@Override
	public ClientPacketHandlerMelonAmbientSound fromBytes(FriendlyByteBuf packet) {
		id = packet.readInt();
		return this;
	}
}

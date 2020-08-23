package tamaized.melongolem.network.client;

import com.mojang.text2speech.Narrator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.MelonSounds;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.network.NetworkMessages;

import javax.annotation.Nullable;

public class ClientPacketHandlerMelonAmbientSound implements NetworkMessages.IMessage<ClientPacketHandlerMelonAmbientSound> {

	private static Narrator narrator;

	private int id;

	public ClientPacketHandlerMelonAmbientSound(EntityMelonGolem golem) {
		id = golem.getEntityId();
	}

	private static void playAmbientSound(PlayerEntity player, EntityMelonGolem golem) {
		SoundEvent soundevent = getAmbientSound();
		if (soundevent != null)
			golem.world.playSound(player, golem.getPosX(), golem.getPosY(), golem.getPosZ(), soundevent, golem.getSoundCategory(), golem.getSoundVolume(), getSoundPitch(golem));
	}

	private static float getSoundPitch(EntityMelonGolem golem) {
		return MelonMod.configClient.tehnutMode.get() ? golem.getPitch() + golem.world.rand.nextFloat() * 0.25F - 0.50F : golem.getSoundPitch();
	}

	@Nullable
	private static SoundEvent getAmbientSound() {
		return MelonMod.configClient.tehnutMode.get() ? MelonSounds.daddy : null;
	}

	@Override
	public void handle(PlayerEntity player) {
		Entity entity = player.world.getEntityByID(id);
		if (entity instanceof EntityMelonGolem) {
			EntityMelonGolem golem = (EntityMelonGolem) entity;
			if (MelonMod.SIGNS.contains(golem.getHead().getItem())) {
				if (MelonMod.configClient.tts.get() && golem.getDistanceSq(player) <= 225) {
					if (narrator == null)
						narrator = Narrator.getNarrator();
					if (!narrator.active())
						return;
					narrator.clear();
					StringBuilder string = new StringBuilder();
					for (int i = 0; i < 4; ++i)
						string.append(TextFormatting.getTextWithoutFormattingCodes(golem.getSignText(i).getString())).append(" ");
					narrator.say(string.toString(), false);
				}
			} else
				playAmbientSound(player, golem);
		}
	}

	@Override
	public void toBytes(PacketBuffer packet) {
		packet.writeInt(id);
	}

	@Override
	public ClientPacketHandlerMelonAmbientSound fromBytes(PacketBuffer packet) {
		id = packet.readInt();
		return this;
	}
}

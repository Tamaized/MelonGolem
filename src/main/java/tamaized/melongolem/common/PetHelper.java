package tamaized.melongolem.common;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;
import tamaized.melongolem.network.client.ClientPacketSendParticles;
import tamaized.melongolem.registry.ModDataAttachments;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class PetHelper {

	@Autowired
	private ModDataAttachments modDataAttachments;

	@Autowired
	private TeleportHelper teleportHelper;

	public boolean summon(ServerLevel level, Player owner) {
		AtomicBoolean summoned = new AtomicBoolean(false);
		TinyGolemAttachment attachment = owner.getData(modDataAttachments.TINY_GOLEM);

		EntityTinyMelonGolem pet = attachment.getPet().orElse(new EntityTinyMelonGolem(level));
		pet.tame(owner);

		teleportHelper.findLocationAboveFriendlyBlock(level, pet, owner.blockPosition()).ifPresent(pos -> {
			pet.snapTo(pos.getCenter());
			ClientPacketSendParticles particles = new ClientPacketSendParticles();
			for (int i = 0; i < 25; i++) {
				Vec3 result = pet.getViewVector(1F).yRot(pet.getRandom().nextFloat() * 360F).xRot(pet.getRandom().nextFloat() * 360F).scale(0.35F);
				particles.queueParticle(ParticleTypes.END_ROD, false, pet.getX() + result.x, pet.getY() + pet.getBbHeight() / 2F + result.y, pet.getZ() + result.z, 0, 0, 0);
			}
			PacketDistributor.sendToPlayersTrackingChunk(level, ChunkPos.containing(owner.blockPosition()), particles);
			if (attachment.getPet().isEmpty())
				level.addFreshEntity(pet);
			attachment.changePet(pet);
			level.playSound(null, pet.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, pet.getRandom().nextFloat() + 0.5F);
			summoned.set(true);
		});
		return summoned.get();
	}

}

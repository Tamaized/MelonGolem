package tamaized.melongolem.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import javax.annotation.Nullable;
import java.util.Optional;

public class TinyGolemAttachment implements ValueIOSerializable {

	@Nullable
	private EntityReference<EntityTinyMelonGolem> pet;

	public TinyGolemAttachment() {

	}

	public void changePet(EntityTinyMelonGolem pet) {
		this.pet = EntityReference.of(pet);
	}

	public Optional<EntityTinyMelonGolem> getPet(Level level) {
		return Optional.ofNullable(pet).map(v -> v.getEntity(level::getEntityInAnyDimension, EntityTinyMelonGolem.class));
	}

	public void tick(Entity owner) {
		getPet(owner.level()).ifPresent(pet -> {
			if (!pet.isAlive()) {
				this.pet = null;
				return;
			}

			if (owner.level() instanceof ServerLevel serverLevel && !pet.level().dimension().equals(owner.level().dimension())) {
				pet.teleport(new TeleportTransition(
					serverLevel,
					owner.position(),
					Vec3.ZERO,
					1F,
					1F,
					TeleportTransition.DO_NOTHING
				));
				pet.tryToTeleportToOwner();
			}
		});
	}

	@Override
	public void serialize(ValueOutput provider) {
		EntityReference.store(pet, provider, "pet");
	}

	@Override
	public void deserialize(ValueInput nbt) {
		pet = EntityReference.read(nbt, "pet");
	}
}

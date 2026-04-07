package tamaized.melongolem.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import tamaized.beanification.Autowired;
import tamaized.beanification.Configurable;

import javax.annotation.Nullable;
import java.util.Optional;

@Configurable
public class TinyGolemAttachment implements ValueIOSerializable {

	@Autowired
	private TeleportHelper teleportHelper;

	private boolean loaded = false;

	@Nullable
	private EntityTinyMelonGolem pet;

	@Nullable
	private Optional<EntityReference<LivingEntity>> petId;

	private int check;

	public TinyGolemAttachment() {

	}

	public boolean isLoaded() {
		return loaded;
	}

	public void changePet(EntityTinyMelonGolem pet) {
		this.pet = pet;
	}

	public Optional<EntityTinyMelonGolem> getPet() {
		return Optional.ofNullable(pet);
	}

	public void tick(Entity owner) {
		if (pet == null && petId.isPresent() && check-- <= 0 && owner.level() instanceof ServerLevel level) {
			if (level.getEntity(petId.get().getUUID()) instanceof EntityTinyMelonGolem tinyMelonGolem) {
				pet = tinyMelonGolem;
				petId = null;
			} else {
				check = 30;
			}
		} else if (pet != null && owner.level() instanceof ServerLevel serverLevel && !pet.level().dimension().equals(serverLevel.dimension())) {
			EntityTinyMelonGolem newPet = new EntityTinyMelonGolem(serverLevel);
			newPet.restoreFrom(pet);
			teleportHelper.findLocationAboveFriendlyBlock(serverLevel, newPet, owner.blockPosition()).ifPresentOrElse(
				pos -> newPet.snapTo(pos, 0, 0),
				() -> newPet.snapTo(owner.position())
			);
			if (owner instanceof Player player)
				newPet.tame(player);
			serverLevel.addFreshEntity(newPet);
			pet.discard();
			pet = newPet;
		} else if (pet != null && !pet.isAlive()) {
			pet = null;
		}
		loaded = true;
	}

	@Override
	public void serialize(ValueOutput provider) {
		if (pet != null)
			EntityReference.store(petId.get(), provider, "pet");
	}

	@Override
	public void deserialize(ValueInput nbt) {
		EntityReference<LivingEntity> ref = EntityReference.readWithOldOwnerConversion(nbt, "pet", pet.level());
		if (ref != null)
			petId = Optional.of(ref);
	}
}

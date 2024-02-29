package tamaized.melongolem.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TinyGolemAttachment implements INBTSerializable<CompoundTag> {

	private boolean loaded = false;

	@Nullable
	private EntityTinyMelonGolem pet;

	@Nullable
	private UUID petId;

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
		if (pet == null && petId != null && check-- <= 0 && owner.level() instanceof ServerLevel level) {
			if (level.getEntity(petId) instanceof EntityTinyMelonGolem tinyMelonGolem) {
				pet = tinyMelonGolem;
				petId = null;
			} else {
				check = 30;
			}
		} else if (pet != null && owner.level() instanceof ServerLevel serverLevel && !pet.level().dimension().equals(owner.level().dimension()) && pet.changeDimension(serverLevel) instanceof EntityTinyMelonGolem newPet) {
			newPet.moveTo(owner.position());
			pet = newPet;
		}
		loaded = true;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		if (pet != null)
			nbt.putUUID("pet", pet.getUUID());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt.contains("pet"))
			petId = nbt.getUUID("pet");
	}
}

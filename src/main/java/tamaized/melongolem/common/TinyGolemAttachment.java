package tamaized.melongolem.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;

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
		} else if (pet != null && owner.level() instanceof ServerLevel serverLevel && !pet.level().dimension().equals(serverLevel.dimension())) {
			boolean noAi = pet.isNoAiEntityDataFlagSet();
			EntityTinyMelonGolem newPet = new EntityTinyMelonGolem(serverLevel);
			newPet.restoreFrom(pet);
			ItemMelonStick.findTeleportFriendlyBlock(serverLevel, newPet, owner.blockPosition()).ifPresentOrElse(pos -> newPet.moveTo(pos, 0, 0), () -> newPet.moveTo(owner.position()));
			if (owner instanceof Player player)
				newPet.tame(player);
			newPet.setNoAi(noAi);
			serverLevel.addFreshEntity(newPet);
			pet.discard();
			pet = newPet;
		} else if (pet != null && !pet.isAlive()) {
			pet = null;
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

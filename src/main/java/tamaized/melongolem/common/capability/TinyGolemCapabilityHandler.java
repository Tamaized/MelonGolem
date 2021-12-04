package tamaized.melongolem.common.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import java.util.UUID;

public class TinyGolemCapabilityHandler implements ITinyGolemCapability {

	private EntityTinyMelonGolem pet;

	private BlockPos vertex;
	private ResourceLocation dim;
	private UUID petID;

	@Override
	public EntityTinyMelonGolem getPet() {
		return pet;
	}

	@Override
	public void setPet(EntityTinyMelonGolem golem) {
		pet = golem;
	}

	@Override
	public void markDirty(BlockPos vertex, ResourceLocation dim, UUID petID) {
		this.vertex = vertex;
		this.dim = dim;
		this.petID = petID;
	}

	@Override
	public ResourceLocation getLoadDim() {
		return dim;
	}

	@Override
	public BlockPos getLoadPos() {
		return vertex;
	}

	@Override
	public UUID getLoadPetID() {
		return petID;
	}

	@Override
	public boolean load(boolean clear) {
		boolean flag = pet == null && vertex != null && petID != null;
		if (clear) {
			dim = Level.OVERWORLD.location();
			vertex = null;
			petID = null;
		}
		return flag;
	}

	@Override
	public void copyFrom(ITinyGolemCapability cap) {
		setPet(cap.getPet());
		markDirty(cap.getLoadPos(), cap.getLoadDim(), cap.getLoadPetID());
	}

}

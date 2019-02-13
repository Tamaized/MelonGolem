package tamaized.melongolem.common.capability;

import net.minecraft.util.math.BlockPos;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import java.util.UUID;

public class TinyGolemCapabilityHandler implements ITinyGolemCapability {

	private EntityTinyMelonGolem pet;

	private BlockPos pos;
	private int dimID;
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
	public void markDirty(BlockPos pos, int dimID, UUID petID) {
		this.pos = pos;
		this.dimID = dimID;
		this.petID = petID;
	}

	@Override
	public int getLoadDimID() {
		return dimID;
	}

	@Override
	public BlockPos getLoadPos() {
		return pos;
	}

	@Override
	public UUID getLoadPetID() {
		return petID;
	}

	@Override
	public boolean load(boolean clear) {
		boolean flag = getPet() == null && pos != null && petID != null;
		if (clear) {
			dimID = 0;
			pos = null;
			petID = null;
		}
		return flag;
	}

	@Override
	public void copyFrom(ITinyGolemCapability cap) {
		setPet(cap.getPet());
		markDirty(cap.getLoadPos(), cap.getLoadDimID(), cap.getLoadPetID());
	}

}

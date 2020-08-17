package tamaized.melongolem.common.capability;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import java.util.UUID;

public class TinyGolemCapabilityHandler implements ITinyGolemCapability {

	private EntityTinyMelonGolem pet;

	private BlockPos pos;
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
	public void markDirty(BlockPos pos, ResourceLocation dim, UUID petID) {
		this.pos = pos;
		this.dim = dim;
		this.petID = petID;
	}

	@Override
	public ResourceLocation getLoadDim() {
		return dim;
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
		boolean flag = pet == null && pos != null && petID != null;
		if (clear) {
			dim = World.OVERWORLD.func_240901_a_();
			pos = null;
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

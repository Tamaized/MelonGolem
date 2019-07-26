package tamaized.melongolem.common.capability;


import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import tamaized.melongolem.common.EntityTinyMelonGolem;

public class TinyGolemCapabilityStorage implements Capability.IStorage<ITinyGolemCapability> {

	@Override
	public INBT writeNBT(Capability<ITinyGolemCapability> capability, ITinyGolemCapability instance, Direction side) {
		CompoundNBT nbt = new CompoundNBT();
		EntityTinyMelonGolem pet = instance.getPet();
		if (pet != null && pet.getOwnerId() != null) {
			nbt.putLong("pos", pet.getPosition().toLong());
			nbt.putInt("dim", pet.world.dimension.getType().getId());
			nbt.putUniqueId("uuid", pet.getUniqueID());
		}
		return nbt;
	}

	@Override
	public void readNBT(Capability<ITinyGolemCapability> capability, ITinyGolemCapability instance, Direction side, INBT nbt) {
		if (nbt instanceof CompoundNBT) {
			CompoundNBT data = (CompoundNBT) nbt;
			if (data.contains("pos") && data.contains("dim") && data.contains("uuid")) {
				instance.markDirty(BlockPos.fromLong(data.getLong("pos")), data.getInt("dim"), data.getUniqueId("uuid"));
			}
		}
	}
}

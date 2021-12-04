package tamaized.melongolem.common.capability;


import net.minecraft.nbt.INBT;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import tamaized.melongolem.common.EntityTinyMelonGolem;

public class TinyGolemCapabilityStorage implements Capability.IStorage<ITinyGolemCapability> {

	@Override
	public INBT writeNBT(Capability<ITinyGolemCapability> capability, ITinyGolemCapability instance, Direction side) {
		CompoundTag nbt = new CompoundTag();
		EntityTinyMelonGolem pet = instance.getPet();
		if (pet != null && pet.getOwnerUUID() != null) {
			nbt.putLong("vertex", pet.getPosition().toLong());
			nbt.putString("dim", pet.level.dimension().location().toString());
			nbt.putUniqueId("uuid", pet.getUUID());
		}
		return nbt;
	}

	@Override
	public void readNBT(Capability<ITinyGolemCapability> capability, ITinyGolemCapability instance, Direction side, INBT nbt) {
		if (nbt instanceof CompoundTag) {
			CompoundTag data = (CompoundTag) nbt;
			if (data.contains("vertex") && data.contains("dim") && data.contains("uuid")) {
				instance.markDirty(BlockPos.fromLong(data.getLong("vertex")), new ResourceLocation(data.getString("dim")), data.getUniqueId("uuid"));
			}
		}
	}
}

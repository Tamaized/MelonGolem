package tamaized.melongolem.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import tamaized.melongolem.common.EntityTinyMelonGolem;

public class TinyGolemCapabilityStorage implements Capability.IStorage<ITinyGolemCapability> {

	@Override
	public NBTBase writeNBT(Capability<ITinyGolemCapability> capability, ITinyGolemCapability instance, EnumFacing side) {
		NBTTagCompound nbt = new NBTTagCompound();
		EntityTinyMelonGolem pet = instance.getPet();
		if (pet != null && pet.getOwnerId() != null) {
			nbt.setLong("pos", pet.getPosition().toLong());
			nbt.setInteger("dim", pet.world.provider.getDimension());
			nbt.setUniqueId("uuid", pet.getPersistentID());
		}
		return nbt;
	}

	@Override
	public void readNBT(Capability<ITinyGolemCapability> capability, ITinyGolemCapability instance, EnumFacing side, NBTBase nbt) {
		if(nbt instanceof NBTTagCompound){
			NBTTagCompound data = (NBTTagCompound) nbt;
			if(data.hasKey("pos") && data.hasKey("dim") && data.hasKey("uuid")){
				instance.markDirty(BlockPos.fromLong(data.getLong("pos")), data.getInteger("dim"), data.getUniqueId("uuid"));
			}
		}
	}
}

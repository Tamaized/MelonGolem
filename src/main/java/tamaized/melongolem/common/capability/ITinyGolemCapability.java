package tamaized.melongolem.common.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import java.util.UUID;

public interface ITinyGolemCapability extends INBTSerializable<CompoundTag> {

	ResourceLocation ID = new ResourceLocation(MelonMod.MODID, "tinygolemcapabilityhandler");

	EntityTinyMelonGolem getPet();

	void setPet(EntityTinyMelonGolem golem);

	void markDirty(BlockPos vertex, ResourceLocation dim, UUID petID);

	ResourceLocation getLoadDim();

	BlockPos getLoadPos();

	UUID getLoadPetID();

	boolean load(boolean clear);

	void copyFrom(ITinyGolemCapability cap);

}

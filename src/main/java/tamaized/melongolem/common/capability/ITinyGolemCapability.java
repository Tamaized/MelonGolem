package tamaized.melongolem.common.capability;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import java.util.UUID;

public interface ITinyGolemCapability {

	ResourceLocation ID = new ResourceLocation(MelonMod.MODID, "tinygolemcapabilityhandler");

	EntityTinyMelonGolem getPet();

	void setPet(EntityTinyMelonGolem golem);

	void markDirty(BlockPos pos, int dim, UUID petID);

	int getLoadDimID();

	BlockPos getLoadPos();

	UUID getLoadPetID();

	boolean load(boolean clear);

	void copyFrom(ITinyGolemCapability cap);

}

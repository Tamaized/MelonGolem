package tamaized.melongolem.common.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import java.util.UUID;

public interface ITinyGolemCapability {

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

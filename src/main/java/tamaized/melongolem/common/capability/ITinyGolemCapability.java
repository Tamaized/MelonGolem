package tamaized.melongolem.common.capability;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import java.util.UUID;

public interface ITinyGolemCapability {

	ResourceLocation ID = new ResourceLocation(MelonMod.MODID, "tinygolemcapabilityhandler");

	EntityTinyMelonGolem getPet();

	void setPet(EntityTinyMelonGolem golem);

	void markDirty(BlockPos pos, ResourceLocation dim, UUID petID);

	ResourceLocation getLoadDim();

	BlockPos getLoadPos();

	UUID getLoadPetID();

	boolean load(boolean clear);

	void copyFrom(ITinyGolemCapability cap);

}

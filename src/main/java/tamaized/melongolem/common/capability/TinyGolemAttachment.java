package tamaized.melongolem.common.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class TinyGolemAttachment {

	public static final Codec<TinyGolemAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockPos.CODEC.optionalFieldOf("vertex").forGetter(obj -> obj.vertex),
			ResourceLocation.CODEC.fieldOf("dim").forGetter(obj -> obj.dim),
			UUIDUtil.CODEC.optionalFieldOf("uuid").forGetter(obj -> obj.petID)
	).apply(instance, TinyGolemAttachment::new));

	private EntityTinyMelonGolem pet;

	private Optional<BlockPos> vertex;
	private ResourceLocation dim;
	private Optional<UUID> petID;

	public TinyGolemAttachment(Optional<BlockPos> vertex, ResourceLocation dim, Optional<UUID> id) {
		this.vertex = vertex;
		this.dim = dim;
		this.petID = id;
	}

	public TinyGolemAttachment() {
		this(Optional.empty(), Level.OVERWORLD.location(), Optional.empty());
	}

	@Nullable
	public EntityTinyMelonGolem getPet() {
		return pet;
	}

	public void setPet(EntityTinyMelonGolem golem) {
		pet = golem;
	}

	public void markDirty(Optional<BlockPos> vertex, ResourceLocation dim, Optional<UUID> petID) {
		this.vertex = vertex;
		this.dim = dim;
		this.petID = petID;
	}

	public ResourceLocation getLoadDim() {
		return dim;
	}

	public Optional<BlockPos> getLoadPos() {
		return vertex;
	}

	public Optional<UUID> getLoadPetID() {
		return petID;
	}

	public boolean load(boolean clear) {
		boolean flag = pet == null && vertex.isPresent() && petID.isPresent();
		if (clear) {
			dim = Level.OVERWORLD.location();
			vertex = Optional.empty();
			petID = Optional.empty();
		}
		return flag;
	}

	public void copyFrom(TinyGolemAttachment cap) {
		setPet(cap.getPet());
		markDirty(cap.getLoadPos(), cap.getLoadDim(), cap.getLoadPetID());
	}
}

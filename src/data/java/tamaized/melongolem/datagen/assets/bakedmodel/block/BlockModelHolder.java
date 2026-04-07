package tamaized.melongolem.datagen.assets.bakedmodel.block;

import net.minecraft.client.data.models.BlockModelGenerators;
import tamaized.melongolem.datagen.assets.bakedmodel.ModelHolder;

public abstract class BlockModelHolder extends ModelHolder {

	public abstract void build(BlockModelGenerators provider);

}

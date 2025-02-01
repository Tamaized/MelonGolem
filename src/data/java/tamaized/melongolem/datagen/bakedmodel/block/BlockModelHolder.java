package tamaized.melongolem.datagen.bakedmodel.block;

import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class BlockModelHolder {

	@Nullable
	private ModelFile model;

	public final Optional<ModelFile> get() {
		return Optional.ofNullable(model);
	}

	protected final void set(ModelFile model) {
		this.model = model;
	}

	public abstract void build(BlockModelProvider provider);

}

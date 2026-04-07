package tamaized.melongolem.datagen.assets.bakedmodel;


import javax.annotation.Nullable;
import java.util.Optional;

public abstract class ModelHolder {

	@Nullable
	private ModelFile model;

	public final Optional<ModelFile> get() {
		return Optional.ofNullable(model);
	}

	protected final void set(ModelFile model) {
		this.model = model;
	}

}

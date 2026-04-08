package tamaized.melongolem.datagen.assets.bakedmodel;


import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class ModelHolder {

	@Nullable
	private Identifier model;

	public final Optional<Identifier> get() {
		return Optional.ofNullable(model);
	}

	protected final void set(Identifier model) {
		this.model = model;
	}

}

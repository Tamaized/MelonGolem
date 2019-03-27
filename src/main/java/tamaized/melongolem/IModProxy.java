package tamaized.melongolem;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public interface IModProxy {

	void init();

	void finish();

	void openSignHolderGui(ISignHolder golem);

	interface ISignHolder {

		ItemStack getHead();

		float getDistance(Entity entityIn);

		ITextComponent getSignText(int index);

		void setSignText(int index, ITextComponent text);

		int getEntityId();
	}

}

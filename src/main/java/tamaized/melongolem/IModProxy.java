package tamaized.melongolem;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public interface IModProxy {

	void preinit();

	void init();

	void postInit();

	void openSignHolderGui(ISignHolder golem);

	interface ISignHolder {

		ItemStack getHead();

		float signHolderDistance(Entity entityIn);

		ITextComponent getSignText(int index);

		void setSignText(int index, ITextComponent text);

		int getEntityId();
	}

}

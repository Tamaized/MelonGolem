package tamaized.melongolem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public interface ISignHolder {

    ItemStack getHead();

	default float distanceTo(Entity entity) {
		return _distanceTo(entity);
	}

    float _distanceTo(Entity entityIn); // Cannot share a name with mojmap

    Component getSignText(int index);

    void setSignText(int index, Component text);

    int networkID();

    boolean glowingText();

    DyeColor getTextColor();
}

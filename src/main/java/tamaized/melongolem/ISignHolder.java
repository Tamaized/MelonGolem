package tamaized.melongolem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import java.util.List;

public interface ISignHolder {

    ItemStack getHead();

	float distanceTo(Entity entity);

	SignBlockEntity getSignTileEntity();

    Component getSignText(int index);

	List<Component> getSignTextList();

    void setSignText(int index, Component text);

    int networkID();

    boolean glowingText();

    DyeColor getTextColor();
}

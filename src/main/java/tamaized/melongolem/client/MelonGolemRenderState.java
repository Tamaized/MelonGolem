package tamaized.melongolem.client;

import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MelonGolemRenderState extends SnowGolemRenderState {
	public final ItemStackRenderState blockState = new ItemStackRenderState();
	public ItemStack head;
	public boolean isTinyMelon;
	public boolean isEnabled;
	public int color;
	public List<Component> signText;
	public DyeColor textColor;
	public boolean isTextGlowing;
}

package tamaized.melongolem.client;

import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class MelonGolemRenderState extends SnowGolemRenderState {

	public ItemStack headStack = ItemStack.EMPTY;

	public boolean isTinyMelon;

	public boolean isEnabled;

	public int color;

	@Nullable
	public SignBlockEntity signTileEntity = null;

	public List<Component> signText = Collections.emptyList();

	public DyeColor textColor = DyeColor.BLACK;

	public boolean isTextGlowing;
}

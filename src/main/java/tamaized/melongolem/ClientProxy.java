package tamaized.melongolem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import tamaized.melongolem.client.GuiEditGolemSign;

import java.util.function.Function;

public class ClientProxy implements IModProxy {

	@Override
	public void init() {

	}

	@Override
	public void finish() {

	}

	@Override
	public void openSignHolderGui(ISignHolder golem) {
		if (golem.getHead().getItem() == Items.SIGN && golem.getDistance(Minecraft.getInstance().player) <= 6)
			Minecraft.getInstance().displayGuiScreen(new GuiEditGolemSign(golem));
	}

	public enum DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite> {
		INSTANCE;

		@Override
		public TextureAtlasSprite apply(ResourceLocation location) {
			return Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());
		}
	}
}

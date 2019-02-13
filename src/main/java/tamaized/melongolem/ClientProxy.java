package tamaized.melongolem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import tamaized.melongolem.client.GuiEditGolemSign;
import tamaized.melongolem.client.RenderMelonGolem;
import tamaized.melongolem.client.RenderMelonSlice;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.common.EntityMelonSlice;
import tamaized.melongolem.common.EntityTinyMelonGolem;

import java.util.function.Function;

public class ClientProxy implements IModProxy {

	@Override
	public void preinit() {
		RenderingRegistry.registerEntityRenderingHandler(EntityMelonGolem.class, RenderMelonGolem.Factory::normal);
		RenderingRegistry.registerEntityRenderingHandler(EntityMelonSlice.class, RenderMelonSlice::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTinyMelonGolem.class, RenderMelonGolem.Factory::tiny);
	}

	@Override
	public void init() {

	}

	@Override
	public void postInit() {

	}

	@Override
	public void openSignHolderGui(ISignHolder golem) {
		if (golem.getHead().getItem() == Items.SIGN && golem.getDistance(Minecraft.getMinecraft().player) <= 6)
			Minecraft.getMinecraft().displayGuiScreen(new GuiEditGolemSign(golem));
	}

	public enum DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite> {
		INSTANCE;

		@Override
		public TextureAtlasSprite apply(ResourceLocation location) {
			return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
		}
	}
}

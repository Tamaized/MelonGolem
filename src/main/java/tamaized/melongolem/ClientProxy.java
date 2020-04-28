package tamaized.melongolem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import tamaized.melongolem.client.GuiEditGolemSign;
import tamaized.melongolem.client.RenderMelonGolem;

public class ClientProxy implements IModProxy {

//	public static final VertexFormat ITEM_FORMAT_WITH_LIGHTMAP = new VertexFormat(DefaultVertexFormats.ITEM).addElement(DefaultVertexFormats.TEX_2S);

	public static void registerRenders() {
		RenderingRegistry.registerEntityRenderingHandler(MelonMod.entityTypeMelonGolem, RenderMelonGolem.Factory::normal);
		RenderingRegistry.registerEntityRenderingHandler(MelonMod.entityTypeMelonSlice, manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
		RenderingRegistry.registerEntityRenderingHandler(MelonMod.entityTypeTinyMelonGolem, RenderMelonGolem.Factory::tiny);
		RenderingRegistry.registerEntityRenderingHandler(MelonMod.entityTypeGlisteringMelonGolem, RenderMelonGolem.Factory::glister);

		RenderTypeLookup.setRenderLayer(MelonMod.glisteringMelonBlock, RenderType.getCutoutMipped());
	}

	@Override
	public void init() {

	}

	@Override
	public void finish() {

	}

	@Override
	public void openSignHolderGui(ISignHolder golem) {
		if (MelonMod.SIGNS.contains(golem.getHead().getItem()) && golem.getDistance(Minecraft.getInstance().player) <= 6)
			Minecraft.getInstance().displayGuiScreen(new GuiEditGolemSign(golem));
	}

	/*public enum DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite> {
		INSTANCE;

		@Override
		public TextureAtlasSprite apply(ResourceLocation location) {
			return Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());
		}
	}*/
}

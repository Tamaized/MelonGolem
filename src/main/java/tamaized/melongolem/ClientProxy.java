package tamaized.melongolem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tamaized.melongolem.client.GuiEditGolemSign;
import tamaized.melongolem.client.RenderMelonGolem;

@Mod.EventBusSubscriber(modid = MelonMod.MODID, value = Dist.CLIENT)
public class ClientProxy implements IModProxy {

//	public static final VertexFormat ITEM_FORMAT_WITH_LIGHTMAP = new VertexFormat(DefaultVertexFormats.ITEM).addElement(DefaultVertexFormats.TEX_2S);

	public static void registerRenders() {
		ItemBlockRenderTypes.setRenderLayer(MelonMod.glisteringMelonBlock, RenderType.cutout());
	}

	@SubscribeEvent
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MelonMod.entityTypeMelonGolem, RenderMelonGolem.Factory::normal);
		event.registerEntityRenderer(MelonMod.entityTypeMelonSlice, ThrownItemRenderer::new);
		event.registerEntityRenderer(MelonMod.entityTypeTinyMelonGolem, RenderMelonGolem.Factory::tiny);
		event.registerEntityRenderer(MelonMod.entityTypeGlisteringMelonGolem, RenderMelonGolem.Factory::glister);
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
			Minecraft.getInstance().setScreen(new GuiEditGolemSign(golem));
	}

	/*public enum DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite> {
		INSTANCE;

		@Override
		public TextureAtlasSprite apply(ResourceLocation location) {
			return Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());
		}
	}*/
}

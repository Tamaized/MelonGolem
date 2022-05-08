package tamaized.melongolem.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.MelonMod;

@Mod.EventBusSubscriber(modid = MelonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientListener {

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MelonMod.ENTITY_TYPE_MELON_GOLEM.get(), RenderMelonGolem.Factory::normal);
        event.registerEntityRenderer(MelonMod.ENTITY_TYPE_MELON_SLICE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(MelonMod.ENTITY_TYPE_TINY_MELON_GOLEM.get(), RenderMelonGolem.Factory::tiny);
        event.registerEntityRenderer(MelonMod.ENTITY_TYPE_GLISTERING_MELON_GOLEM.get(), RenderMelonGolem.Factory::glister);
    }

    public static void registerRenders() {
        ItemBlockRenderTypes.setRenderLayer(MelonMod.BLOCK_GLISTERING_MELON.get(), RenderType.cutout());
    }

    public static void openSignHolderGui(ISignHolder golem) {
        if (MelonMod.SIGNS.contains(golem.getHead().getItem()) && golem.distanceTo(Minecraft.getInstance().player) <= 6)
            Minecraft.getInstance().setScreen(new GuiEditGolemSign(golem));
    }
}

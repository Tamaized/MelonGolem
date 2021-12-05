package tamaized.melongolem.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tamaized.melongolem.IModProxy;
import tamaized.melongolem.MelonMod;

@Mod.EventBusSubscriber(modid = MelonMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientListener {

    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MelonMod.entityTypeMelonGolem, RenderMelonGolem.Factory::normal);
        event.registerEntityRenderer(MelonMod.entityTypeMelonSlice, ThrownItemRenderer::new);
        event.registerEntityRenderer(MelonMod.entityTypeTinyMelonGolem, RenderMelonGolem.Factory::tiny);
        event.registerEntityRenderer(MelonMod.entityTypeGlisteringMelonGolem, RenderMelonGolem.Factory::glister);
    }

    public static void registerRenders() {
        ItemBlockRenderTypes.setRenderLayer(MelonMod.glisteringMelonBlock, RenderType.cutout());
    }

    public static void openSignHolderGui(IModProxy.ISignHolder golem) {
        if (MelonMod.SIGNS.contains(golem.getHead().getItem()) && golem.distanceTo(Minecraft.getInstance().player) <= 6)
            Minecraft.getInstance().setScreen(new GuiEditGolemSign(golem));
    }
}

package tamaized.melongolem.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.tags.ItemTags;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
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

    public static void openSignHolderGui(ISignHolder golem) {
        if (golem.getHead().is(ItemTags.SIGNS) && golem.distanceTo(Minecraft.getInstance().player) <= 6)
            Minecraft.getInstance().setScreen(new GuiEditGolemSign(golem));
    }
}

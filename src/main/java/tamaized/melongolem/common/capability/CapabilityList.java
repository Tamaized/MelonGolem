package tamaized.melongolem.common.capability;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import tamaized.melongolem.MelonMod;

@Mod.EventBusSubscriber(modid = MelonMod.MODID)
public class CapabilityList {

	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MelonMod.MODID);

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<TinyGolemAttachment>> TINY_GOLEM = ATTACHMENT_TYPES.register("tinygolemcapabilityhandler",
			() -> AttachmentType.builder(TinyGolemAttachment::new).serialize(TinyGolemAttachment.CODEC).build());

	@SubscribeEvent
	public static void updateClone(PlayerEvent.Clone e) {
		e.getEntity().getData(TINY_GOLEM).copyFrom(e.getOriginal().getData(TINY_GOLEM));
	}
}

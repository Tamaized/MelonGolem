package tamaized.melongolem.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import tamaized.melongolem.common.TinyGolemAttachment;
import tamaized.regutil.RegUtil;
import tamaized.regutil.RegistryClass;

import java.util.function.Supplier;

public class ModDataAttachments implements RegistryClass {

	private static final DeferredRegister<AttachmentType<?>> REGISTRY = RegUtil.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES);

	public static final Supplier<AttachmentType<TinyGolemAttachment>> TINY_GOLEM = REGISTRY.register("tiny_golem", () -> AttachmentType.serializable(TinyGolemAttachment::new).build());

	@Override
	public void init(IEventBus bus) {
		NeoForge.EVENT_BUS.addListener(LivingEvent.LivingTickEvent.class, event -> event.getEntity().getData(TINY_GOLEM).tick(event.getEntity()));
	}

}

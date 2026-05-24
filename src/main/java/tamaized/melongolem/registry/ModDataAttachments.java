package tamaized.melongolem.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import tamaized.beanification.Component;
import tamaized.beanification.PostConstruct;
import tamaized.melongolem.common.TinyGolemAttachment;
import tamaized.regutil.RegUtil;

import java.util.function.Supplier;

@Component
public class ModDataAttachments {

	public final Supplier<AttachmentType<TinyGolemAttachment>> TINY_GOLEM = RegUtil.register(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "tiny_golem",
		() -> AttachmentType.serializable(TinyGolemAttachment::new).build()
	);

	@PostConstruct(PostConstruct.Bus.GAME)
	private void setup(IEventBus bus) {
		bus.addListener(EntityTickEvent.Post.class, event -> event.getEntity().getData(TINY_GOLEM).tick(event.getEntity()));
	}

}

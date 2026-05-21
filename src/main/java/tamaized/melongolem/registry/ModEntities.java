package tamaized.melongolem.registry;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tamaized.beanification.Component;
import tamaized.beanification.PostConstruct;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.client.RenderMelonGolem;
import tamaized.melongolem.common.EntityGlisteringMelonGolem;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.common.EntityMelonSlice;
import tamaized.melongolem.common.EntityTinyMelonGolem;
import tamaized.regutil.RegUtil;

import java.util.function.Supplier;

@Component
public class ModEntities {

	private final DeferredRegister<EntityType<?>> REGISTRY = RegUtil.create(Registries.ENTITY_TYPE);
	private final DeferredRegister<Item> ITEM_REGISTRY = RegUtil.create(Registries.ITEM);

	public final Supplier<EntityType<EntityMelonGolem>> MELON_GOLEM = REGISTRY.register(
		"melon_golem",
		() -> make(Identifier.fromNamespaceAndPath(MelonMod.MODID, "melon_golem"), EntityMelonGolem::new, MobCategory.CREATURE, 0.7F, 1.9F, 1.7F)
	);
	public final DeferredHolder<Item, SpawnEggItem> SPAWN_EGG_MELON_GOLEM = ITEM_REGISTRY.register(
		"melon_golem_spawn_egg",
		(id) -> new SpawnEggItem(/*0x00FF00, 0x000000, */new Item.Properties()
			.setId(ResourceKey.create(Registries.ITEM, id))
			.spawnEgg(MELON_GOLEM.get())
		)
	);

	public final Supplier<EntityType<EntityGlisteringMelonGolem>> GLISTERING_MELON_GOLEM = REGISTRY.register(
		"glistering_melon_golem",
		() -> make(Identifier.fromNamespaceAndPath(MelonMod.MODID, "glistering_melon_golem"), EntityGlisteringMelonGolem::new, MobCategory.CREATURE, 0.7F, 1.9F, 1.7F)
	);
	public final DeferredHolder<Item, SpawnEggItem> SPAWN_EGG_GLISTERING_MELON_GOLEM = ITEM_REGISTRY.register(
		"glistering_melon_golem_spawn_egg",
		(id) -> new SpawnEggItem(/*0xAAFF00, 0xFFCC00, */new Item.Properties()
			.setId(ResourceKey.create(Registries.ITEM, id))
			.spawnEgg(GLISTERING_MELON_GOLEM.get())
		)
	);

	public final Supplier<EntityType<EntityTinyMelonGolem>> TINY_MELON_GOLEM = REGISTRY.register(
		"tiny_melon_golem",
		() -> make(Identifier.fromNamespaceAndPath(MelonMod.MODID, "tiny_melon_golem"), EntityTinyMelonGolem::new, MobCategory.CREATURE, 0.175F, 0.475F, 0.425F)
	);

	public final Supplier<EntityType<EntityMelonSlice>> MELON_SLICE = REGISTRY.register(
		"melon_slice",
		() -> make(Identifier.fromNamespaceAndPath(MelonMod.MODID, "melon_slice"), EntityMelonSlice::new, MobCategory.MISC, 0.25F, 0.25F)
	);

	private <E extends Entity> EntityType<E> make(Identifier id, EntityType.EntityFactory<E> factory, MobCategory classification, float width, float height) {
		return build(id, makeBuilder(factory, classification).sized(width, height));
	}

	private <E extends Entity> EntityType<E> make(Identifier id, EntityType.EntityFactory<E> factory, MobCategory classification, float width, float height, float eyeHeight) {
		return build(id, makeBuilder(factory, classification).sized(width, height).eyeHeight(eyeHeight));
	}

	private <E extends Entity> EntityType<E> make(Identifier id, EntityType.EntityFactory<E> factory, MobCategory classification) {
		return make(id, factory, classification, 0.6F, 1.8F);
	}

	private <E extends Entity> EntityType<E> build(Identifier id, EntityType.Builder<E> builder) {
		return builder.build(ResourceKey.create(Registries.ENTITY_TYPE, id));
	}

	private <E extends Entity> EntityType.Builder<E> makeCastedBuilder(Class<E> cast, EntityType.EntityFactory<E> factory, MobCategory classification) {
		return makeBuilder(factory, classification);
	}

	private <E extends Entity> EntityType.Builder<E> makeBuilder(EntityType.EntityFactory<E> factory, MobCategory classification) {
		return EntityType.Builder.of(factory, classification).
			sized(0.6F, 1.8F).
			setTrackingRange(80).
			setUpdateInterval(3).
			setShouldReceiveVelocityUpdates(true);
	}

	@PostConstruct
	public void init(IEventBus bus) {
		bus.addListener(EntityAttributeCreationEvent.class, this::registerAttributes);
		if (FMLEnvironment.getDist() == Dist.CLIENT) {
			bus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, this::registerLayerDefinitions);
			bus.addListener(EntityRenderersEvent.RegisterRenderers.class, this::registerEntityRenderer);
		}
	}

	private void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(MELON_GOLEM.get(), EntityMelonGolem.createAttributes().build());
		event.put(GLISTERING_MELON_GOLEM.get(), EntityMelonGolem.createAttributes().build());
		event.put(TINY_MELON_GOLEM.get(), EntityMelonGolem.createAttributes().build());
	}

	private void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {


	}

	private void registerEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MELON_GOLEM.get(), RenderMelonGolem.Factory::normal);
		event.registerEntityRenderer(TINY_MELON_GOLEM.get(), RenderMelonGolem.Factory::tiny);
		event.registerEntityRenderer(GLISTERING_MELON_GOLEM.get(), RenderMelonGolem.Factory::glister);
		event.registerEntityRenderer(MELON_SLICE.get(), ThrownItemRenderer::new);
	}

}

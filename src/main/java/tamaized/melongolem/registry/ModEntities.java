package tamaized.melongolem.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.client.RenderMelonGolem;
import tamaized.melongolem.common.EntityGlisteringMelonGolem;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.common.EntityMelonSlice;
import tamaized.melongolem.common.EntityTinyMelonGolem;
import tamaized.regutil.RegUtil;
import tamaized.regutil.RegistryClass;

import java.util.function.Supplier;

public class ModEntities implements RegistryClass {

	private static final DeferredRegister<EntityType<?>> REGISTRY = RegUtil.create(Registries.ENTITY_TYPE);

	public static final Supplier<EntityType<EntityMelonGolem>> MELON_GOLEM = REGISTRY
			.register("melon_golem", () -> make(new ResourceLocation(MelonMod.MODID, "melon_golem"), EntityMelonGolem::new, MobCategory.CREATURE, 0.7F, 1.9F));
	public static final Supplier<Item> SPAWN_EGG_MELON_GOLEM = ModItems.REGISTRY
			.register("melon_golem_spawn_egg", () -> new DeferredSpawnEggItem(MELON_GOLEM, 0x00FF00, 0x000000, new Item.Properties()));

	public static final Supplier<EntityType<EntityGlisteringMelonGolem>> GLISTERING_MELON_GOLEM = REGISTRY
			.register("glistering_melon_golem", () -> make(new ResourceLocation(MelonMod.MODID, "glistering_melon_golem"), EntityGlisteringMelonGolem::new, MobCategory.CREATURE, 0.7F, 1.9F));
	public static final Supplier<Item> SPAWN_EGG_GLISTERING_MELON_GOLEM = ModItems.REGISTRY
			.register("glistering_melon_golem_spawn_egg", () -> new DeferredSpawnEggItem(GLISTERING_MELON_GOLEM, 0xAAFF00, 0xFFCC00, new Item.Properties()));

	public static final Supplier<EntityType<EntityTinyMelonGolem>> TINY_MELON_GOLEM = REGISTRY
			.register("tiny_melon_golem", () -> make(new ResourceLocation(MelonMod.MODID, "tiny_melon_golem"), EntityTinyMelonGolem::new, MobCategory.CREATURE, 0.175F, 0.475F));

	public static final Supplier<EntityType<EntityMelonSlice>> MELON_SLICE = REGISTRY
			.register("melon_slice", () -> make(new ResourceLocation(MelonMod.MODID, "melon_slice"), EntityMelonSlice::new, MobCategory.MISC, 0.25F, 0.25F));



	private static <E extends Entity> EntityType<E> make(ResourceLocation id, EntityType.EntityFactory<E> factory, MobCategory classification, float width, float height) {
		return build(id, makeBuilder(factory, classification).sized(width, height));
	}

	private static <E extends Entity> EntityType<E> make(ResourceLocation id, EntityType.EntityFactory<E> factory, MobCategory classification) {
		return make(id, factory, classification, 0.6F, 1.8F);
	}

	private static <E extends Entity> EntityType<E> build(ResourceLocation id, EntityType.Builder<E> builder) {
		return builder.build(id.toString());
	}

	private static <E extends Entity> EntityType.Builder<E> makeCastedBuilder(Class<E> cast, EntityType.EntityFactory<E> factory, MobCategory classification) {
		return makeBuilder(factory, classification);
	}

	private static <E extends Entity> EntityType.Builder<E> makeBuilder(EntityType.EntityFactory<E> factory, MobCategory classification) {
		return EntityType.Builder.of(factory, classification).
				sized(0.6F, 1.8F).
				setTrackingRange(80).
				setUpdateInterval(3).
				setShouldReceiveVelocityUpdates(true);
	}

	public void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(MELON_GOLEM.get(), EntityMelonGolem.createAttributes().build());
		event.put(GLISTERING_MELON_GOLEM.get(), EntityMelonGolem.createAttributes().build());
		event.put(TINY_MELON_GOLEM.get(), EntityMelonGolem.createAttributes().build());
	}

	@OnlyIn(Dist.CLIENT)
	public static class ModelLayerLocations {

		private static ModelLayerLocation make(String name) {
			return new ModelLayerLocation(new ResourceLocation(MelonMod.MODID, "main"), name);
		}

	}

	@OnlyIn(Dist.CLIENT)
	public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {


	}

	@OnlyIn(Dist.CLIENT)
	public void registerEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MELON_GOLEM.get(), RenderMelonGolem.Factory::normal);
		event.registerEntityRenderer(TINY_MELON_GOLEM.get(), RenderMelonGolem.Factory::tiny);
		event.registerEntityRenderer(GLISTERING_MELON_GOLEM.get(), RenderMelonGolem.Factory::glister);
		event.registerEntityRenderer(MELON_SLICE.get(), ThrownItemRenderer::new);
	}

	@Override
	public void init(IEventBus bus) {
		bus.addListener(EntityAttributeCreationEvent.class, this::registerAttributes);
		if (FMLEnvironment.dist == Dist.CLIENT) {
			bus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, this::registerLayerDefinitions);
			bus.addListener(EntityRenderersEvent.RegisterRenderers.class, this::registerEntityRenderer);
		}
	}

}

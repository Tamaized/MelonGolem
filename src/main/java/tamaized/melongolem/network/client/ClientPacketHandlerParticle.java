package tamaized.melongolem.network.client;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import tamaized.melongolem.MelonMod;

public record ClientPacketHandlerParticle(ResourceLocation location, Vec3 vec, Vec3 vel) implements CustomPacketPayload {

	public static final ResourceLocation ID = new ResourceLocation(MelonMod.MODID, "spawn_particle");

	public ClientPacketHandlerParticle(FriendlyByteBuf buf) {
		this(buf.readResourceLocation(),
				new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
				new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public static void handle(final ClientPacketHandlerParticle packet, PlayPayloadContext context) {
		context.workHandler().execute(() -> context.player().ifPresent(player -> spawnParticle(player.level(), getRegisteredParticleTypes(packet.location()), packet.vec(), packet.vel())));
	}

	public static void spawnParticle(Level world, ParticleOptions particle, Vec3 vertex, Vec3 vel) {
		world.addParticle(particle, vertex.x, vertex.y, vertex.z, vel.x, vel.y, vel.z);
	}

	private static ParticleOptions getRegisteredParticleTypes(ResourceLocation location) {
		ParticleType<?> t = BuiltInRegistries.PARTICLE_TYPE.get(location);
		if (!(t instanceof ParticleOptions)) {
			throw new IllegalStateException("Invalid or unknown particle type: " + location);
		} else {
			return (ParticleOptions) t;
		}
	}

	@Override
	public void write(FriendlyByteBuf packet) {
		packet.writeResourceLocation(location);
		packet.writeDouble(vec.x);
		packet.writeDouble(vec.y);
		packet.writeDouble(vec.z);
		packet.writeDouble(vel.x);
		packet.writeDouble(vel.y);
		packet.writeDouble(vel.z);
	}
}
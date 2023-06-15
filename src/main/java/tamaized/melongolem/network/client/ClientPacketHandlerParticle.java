package tamaized.melongolem.network.client;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import tamaized.melongolem.network.NetworkMessages;

public class ClientPacketHandlerParticle implements NetworkMessages.IMessage<ClientPacketHandlerParticle> {

	private ResourceLocation id;
	private Vec3 vec;
	private Vec3 vel;

	public ClientPacketHandlerParticle(ResourceLocation particle, Vec3 vertex, Vec3 vel) {
		id = particle;
		vec = vertex;
		this.vel = vel;
	}

	public static void spawnParticle(Level world, ParticleOptions particle, Vec3 vertex, Vec3 vel) {
		world.addParticle(particle, vertex.x, vertex.y, vertex.z, vel.x, vel.y, vel.z);
	}

	private static ParticleOptions getRegisteredParticleTypes(ResourceLocation location) {
		ParticleType<?> t = ForgeRegistries.PARTICLE_TYPES.getValue(location);
		if (!(t instanceof ParticleOptions)) {
			throw new IllegalStateException("Invalid or unknown particle type: " + location);
		} else {
			return (ParticleOptions) t;
		}
	}

	@Override
	public void handle(Player player) {
		spawnParticle(player.level(), getRegisteredParticleTypes(id), vec, vel);
	}

	@Override
	public void toBytes(FriendlyByteBuf packet) {
		packet.writeResourceLocation(id);
		packet.writeDouble(vec.x);
		packet.writeDouble(vec.y);
		packet.writeDouble(vec.z);
		packet.writeDouble(vel.x);
		packet.writeDouble(vel.y);
		packet.writeDouble(vel.z);
	}

	@Override
	public ClientPacketHandlerParticle fromBytes(FriendlyByteBuf packet) {
		id = packet.readResourceLocation();
		vec = new Vec3(packet.readDouble(), packet.readDouble(), packet.readDouble());
		vel = new Vec3(packet.readDouble(), packet.readDouble(), packet.readDouble());
		return this;
	}
}
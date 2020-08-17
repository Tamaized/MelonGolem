package tamaized.melongolem.network.client;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import tamaized.melongolem.network.NetworkMessages;

public class ClientPacketHandlerParticle implements NetworkMessages.IMessage<ClientPacketHandlerParticle> {

	private ResourceLocation id;
	private Vector3d vec;
	private Vector3d vel;

	public ClientPacketHandlerParticle(ResourceLocation particle, Vector3d pos, Vector3d vel) {
		id = particle;
		vec = pos;
		this.vel = vel;
	}

	public static void spawnParticle(World world, IParticleData particle, Vector3d pos, Vector3d vel) {
		world.addParticle(particle, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
	}

	private static IParticleData getRegisteredParticleTypes(ResourceLocation p_197589_0_) {
		ParticleType<?> t = ForgeRegistries.PARTICLE_TYPES.getValue(p_197589_0_);
		if (!(t instanceof IParticleData)) {
			throw new IllegalStateException("Invalid or unknown particle type: " + p_197589_0_);
		} else {
			return (IParticleData) t;
		}
	}

	@Override
	public void handle(PlayerEntity player) {
		spawnParticle(player.world, getRegisteredParticleTypes(id), vec, vel);
	}

	@Override
	public void toBytes(PacketBuffer packet) {
		packet.writeResourceLocation(id);
		packet.writeDouble(vec.x);
		packet.writeDouble(vec.y);
		packet.writeDouble(vec.z);
		packet.writeDouble(vel.x);
		packet.writeDouble(vel.y);
		packet.writeDouble(vel.z);
	}

	@Override
	public ClientPacketHandlerParticle fromBytes(PacketBuffer packet) {
		id = packet.readResourceLocation();
		vec = new Vector3d(packet.readDouble(), packet.readDouble(), packet.readDouble());
		vel = new Vector3d(packet.readDouble(), packet.readDouble(), packet.readDouble());
		return this;
	}
}
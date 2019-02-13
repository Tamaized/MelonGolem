package tamaized.melongolem.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientPacketHandlerParticle implements IMessageHandler<ClientPacketHandlerParticle.Packet, IMessage> {

	@SideOnly(Side.CLIENT)
	private static void processPacket(Packet message, EntityPlayer player, World world) {
		spawnParticle(world, EnumParticleTypes.getParticleFromId(message.id), message.vec, message.vel);
	}

	public static void spawnParticle(World world, EnumParticleTypes particle, Vec3d pos, Vec3d vel) {
		world.spawnParticle(particle, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(Packet message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> processPacket(message, Minecraft.getMinecraft().player, Minecraft.getMinecraft().world));
		return null;
	}

	public static class Packet implements IMessage {

		private int id;
		private Vec3d vec;
		private Vec3d vel;

		public Packet() {

		}

		public Packet(int particle, Vec3d pos, Vec3d vel) {
			id = particle;
			vec = pos;
			this.vel = vel;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			id = buf.readInt();
			vec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
			vel = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(id);
			buf.writeDouble(vec.x);
			buf.writeDouble(vec.y);
			buf.writeDouble(vec.z);
			buf.writeDouble(vel.x);
			buf.writeDouble(vel.y);
			buf.writeDouble(vel.z);
		}
	}
}
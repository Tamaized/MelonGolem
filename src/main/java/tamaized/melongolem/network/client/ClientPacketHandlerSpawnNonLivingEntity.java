package tamaized.melongolem.network.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tamaized.melongolem.network.NetworkMessages;

import java.util.Objects;
import java.util.UUID;

// hi-jacked SSpawnObjectPacket TODO: Delete later
public class ClientPacketHandlerSpawnNonLivingEntity implements NetworkMessages.IMessage<ClientPacketHandlerSpawnNonLivingEntity> {

	private int entityId;
	private UUID uniqueId;
	private double x;
	private double y;
	private double z;
	private int speedX;
	private int speedY;
	private int speedZ;
	private int pitch;
	private int yaw;
	private EntityType<?> type;

	public ClientPacketHandlerSpawnNonLivingEntity(Entity entity) {
		this.entityId = entity.getId();
		this.uniqueId = entity.getUUID();
		this.x = entity.getX();
		this.y = entity.getY();
		this.z = entity.getZ();
		this.pitch = Mth.floor(entity.getXRot() * 256.0F / 360.0F);
		this.yaw = Mth.floor(entity.getYRot() * 256.0F / 360.0F);
		this.type = entity.getType();
		Vec3 motion = entity.getDeltaMovement();
		this.speedX = (int) (Mth.clamp(motion.x, -3.9D, 3.9D) * 8000.0D);
		this.speedY = (int) (Mth.clamp(motion.y, -3.9D, 3.9D) * 8000.0D);
		this.speedZ = (int) (Mth.clamp(motion.z, -3.9D, 3.9D) * 8000.0D);
	}

	@Override
	public void handle(Player player) {
		Level world = player.level;
		Entity entity = type.create(world);
		Objects.requireNonNull(entity).setPacketCoordinates(x, y, z);
		entity.setDeltaMovement(speedX, speedY, speedZ);
		entity.setXRot((float) (pitch * 360) / 256.0F);
		entity.setYRot((float) (yaw * 360) / 256.0F);
		entity.setId(entityId);
		entity.setUUID(uniqueId);
		//((ClientLevel) world).addFreshEntity(entityId, entity);
	}

	@Override
	public void toBytes(FriendlyByteBuf packet) {
		packet.writeVarInt(this.entityId);
		packet.writeUUID(this.uniqueId);
		packet.writeVarInt(Registry.ENTITY_TYPE.getId(this.type));
		packet.writeDouble(this.x);
		packet.writeDouble(this.y);
		packet.writeDouble(this.z);
		packet.writeByte(this.pitch);
		packet.writeByte(this.yaw);
		packet.writeShort(this.speedX);
		packet.writeShort(this.speedY);
		packet.writeShort(this.speedZ);
	}

	@Override
	public ClientPacketHandlerSpawnNonLivingEntity fromBytes(FriendlyByteBuf packet) {
		this.entityId = packet.readVarInt();
		this.uniqueId = packet.readUUID();
		this.type = Registry.ENTITY_TYPE.byId(packet.readVarInt());
		this.x = packet.readDouble();
		this.y = packet.readDouble();
		this.z = packet.readDouble();
		this.pitch = packet.readByte();
		this.yaw = packet.readByte();
		this.speedX = packet.readShort();
		this.speedY = packet.readShort();
		this.speedZ = packet.readShort();
		return this;
	}
}

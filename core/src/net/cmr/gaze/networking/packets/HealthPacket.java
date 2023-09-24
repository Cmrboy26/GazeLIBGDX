package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.util.UuidUtils;
import net.cmr.gaze.world.entities.HealthEntity;

@PacketID(id = 24)
public class HealthPacket extends Packet {

	UUID entityUUID;
	int health;
	
	public HealthPacket(HealthEntity entity) {
		entityUUID = entity.getUUID();
		health = entity.getHealth();
	}
	
	public HealthPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public UUID getEntityUUID() {
		return entityUUID;
	}
	public int getHealth() {
		return health;
	}
	
	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		UuidUtils.writeUUID(buffer, entityUUID);
		buffer.writeInt(health);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		entityUUID = UuidUtils.readUUID(input);
		health = input.readInt();
	}
	
	
	
}

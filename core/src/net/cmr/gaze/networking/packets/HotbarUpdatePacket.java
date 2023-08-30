package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.util.UuidUtils;

@PacketID(id = 14)
public class HotbarUpdatePacket extends Packet {

	int slot;
	UUID entityID;
	
	public HotbarUpdatePacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	// Clientbound
	public HotbarUpdatePacket(UUID entityID, byte slot) {
		this.slot = slot;
		this.entityID = entityID;
	}
	// Serverbound
	public HotbarUpdatePacket(byte slot) {
		this.slot = slot;
	}
	
	public int getSlot() {
		return slot;
	}
	public UUID getEntityID() {
		return entityID;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.write((byte) slot);
		UuidUtils.writeUUID(buffer, entityID);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		slot = input.read();
		entityID = UuidUtils.readUUID(input);
	}

}

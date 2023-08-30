package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.util.UuidUtils;
import net.cmr.gaze.world.entities.Entity;

@PacketID(id = 11)
public class DespawnEntity extends Packet {

	UUID uuid;
	
	public DespawnEntity(Entity entity) {
		this.uuid = entity.getUUID();
	}
	
	public DespawnEntity(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public UUID getUUID() {
		return uuid;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		byte[] bytes = UuidUtils.asBytes(uuid);
		buffer.writeInt(bytes.length);
		buffer.write(bytes);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		byte[] bytes = new byte[input.readInt()];
		input.read(bytes);
		uuid = UuidUtils.asUuid(bytes);
	}

}
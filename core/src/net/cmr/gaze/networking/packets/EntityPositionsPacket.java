package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.CompressedPacket;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.util.UuidUtils;
import net.cmr.gaze.util.Vector2Double;
import net.cmr.gaze.world.entities.Entity;

@PacketID(id = 12)
public class EntityPositionsPacket extends CompressedPacket {

	HashMap<UUID, Vector2Double> positions, velocities;
	
	public EntityPositionsPacket(ArrayList<Entity> entities) {
		positions = new HashMap<>();
		velocities = new HashMap<>();
		for(Entity ent : entities) {
			UUID id = ent.getUUID();
			Vector2Double pos = new Vector2Double(ent.getX(), ent.getY());
			Vector2Double vel = new Vector2Double(ent.getVelocityX(), ent.getVelocityY());
			positions.put(id, pos);
			velocities.put(id, vel);
		}
	}
	
	public EntityPositionsPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public HashMap<UUID, Vector2Double> getPositions() {
		return positions;
	}
	public HashMap<UUID, Vector2Double> getVelocities() {
		return velocities;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeInt(positions.size());
		int uuidByteLength = -1;
		for(UUID uuid : positions.keySet()) {
			byte[] uuidBytes = UuidUtils.asBytes(uuid);
			
			if(uuidByteLength==-1) {
				uuidByteLength = uuidBytes.length;
				buffer.writeInt(uuidByteLength);
			}
			
			buffer.write(uuidBytes);
			
			Vector2Double doubles = positions.get(uuid);
			Vector2Double velocity = velocities.get(uuid);
			buffer.writeDouble(doubles.getX());
			buffer.writeDouble(doubles.getY());
			buffer.writeDouble(velocity.getX());
			buffer.writeDouble(velocity.getY());
		}
	}

	@Override
	public void readDecompressedPacketData(DataInputStream input, int packetSize) throws IOException {

		int entriesLength = input.readInt();
		positions = new HashMap<>(entriesLength);
		velocities = new HashMap<>(entriesLength);
		
		if(entriesLength==0) {
			return;
		}
		
		int uuidByteLength = input.readInt();
		for(int i = 0; i < entriesLength; i++) {
			byte[] uuidBytes = new byte[uuidByteLength];
			input.read(uuidBytes);
			
			UUID uuid = UuidUtils.asUuid(uuidBytes);
			Vector2Double doubles = new Vector2Double(input.readDouble(), input.readDouble());
			Vector2Double velocity = new Vector2Double(input.readDouble(), input.readDouble());
			positions.put(uuid, doubles);
			velocities.put(uuid, velocity);
			//System.out.println(uuid.toString()+":"+doubles);
		}
	}

}

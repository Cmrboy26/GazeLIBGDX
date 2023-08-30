package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.world.entities.Entity;

@PacketID(id = 10)
public class SpawnEntity extends Packet {

	Entity entity;
	boolean obfuscatePosition;
	
	public SpawnEntity(Entity entity) {
		this.entity = entity;
		this.obfuscatePosition = false;
	}
	public SpawnEntity(Entity entity, boolean obfuscatePosition) {
		this.entity = entity;
		this.obfuscatePosition = obfuscatePosition;
	}
	
	public SpawnEntity(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public Entity getEntity() {
		return entity;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		entity.writeEntity(buffer, obfuscatePosition, false);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		entity = Entity.readEntity(input, false);
	}

}

package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.WorldGenerator.WorldGeneratorType;

@PacketID(id = 18)
public class WorldChangePacket extends Packet {

	WorldGeneratorType type;
	double time;
	
	public WorldChangePacket(World world) {
		this.type = world.getGenerator().getGeneratorType();
		this.time = world.getWorldTime();
	}
	
	public WorldChangePacket(DataInputStream input, int nextPacketSize) throws IOException {
		super(input, nextPacketSize);
	}

	public WorldGeneratorType getType() {
		return type;
	}
	public double getTime() {
		return time;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeInt(type.getID());
		buffer.writeDouble(time);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		type = WorldGeneratorType.getTypeFromID(input.readInt());
		time = input.readDouble();
	}

}

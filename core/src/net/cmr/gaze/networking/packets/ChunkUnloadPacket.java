package net.cmr.gaze.networking.packets;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 9)
public class ChunkUnloadPacket extends Packet {

	Point chunkCoordinate;
	
	public ChunkUnloadPacket(Point chunkCoordinate) {
		this.chunkCoordinate = chunkCoordinate;
	}
	public ChunkUnloadPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public Point getCoordinate() {
		return chunkCoordinate;
	}
	

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeInt(chunkCoordinate.x);
		buffer.writeInt(chunkCoordinate.y);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		chunkCoordinate = new Point(input.readInt(), input.readInt());
	}

	
	
}

package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.util.Vector2Double;

@PacketID(id = 4)
public class PositionPacket extends Packet {

	Vector2Double position;
	String worldName;
	
	public PositionPacket(Vector2Double position) {
		this.position = position;
	}
	
	public PositionPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeDouble(position.getX());
		buffer.writeDouble(position.getY());
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		position = new Vector2Double(input.readDouble(), input.readDouble());
	}

	public double getX() {
		return position.getX();
	}
	public double getY() {
		return position.getY();
	}
	
}

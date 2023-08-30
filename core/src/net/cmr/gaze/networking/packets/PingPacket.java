package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 2)
public class PingPacket extends Packet {

	long time;
	boolean server;
	public PingPacket(long time, boolean server) {
		this.time = time;
		this.server = server;
	}
	public PingPacket(DataInputStream stream, int packetSize) throws IOException {
		super(stream, packetSize);
	}
	
	public long getTime() {
		return time;
	}
	public boolean isServerPing() {
		return server;
	}
	
	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeLong(getTime());
		buffer.writeBoolean(isServerPing());
	}
	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		this.time = input.readLong();
		this.server = input.readBoolean();
	}

	/*@Override
	public int getPacketIdentifier() {
		return getIdentifier();
	}
	
	public static short getIdentifier() {
		return 2;
	}
	
	public static void writePacket(DataOutputStream out, long time) throws IOException {
		DataBuffer buffer = new DataBuffer();
		
		buffer.writeLong(time);
		
		sendPacketData(out, getIdentifier(), buffer);
	}
	
	public static PingPacket readPacket(DataInputStream input) throws IOException{
		return new PingPacket(input.readLong());
	}
*/
}

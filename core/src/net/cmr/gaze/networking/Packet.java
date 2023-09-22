package net.cmr.gaze.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

@PacketID(id = -1)
public abstract class Packet {
	
	
	public Packet() {
		
	}
	
	public Packet(DataInputStream input, int packetSize) throws IOException {
		readPacketData(input, packetSize);
	}
	
	public static int readID(Class<? extends Packet> clazz) {
		return clazz.getAnnotation(PacketID.class).id();
	}
	
	public final int getPacketIdentifier() {
		return readID(getClass());
	}
	
	public int sendPacket(DataOutputStream output) throws IOException {
		DataBuffer buffer = new DataBuffer();
		writePacketData(buffer);
		output.writeInt(getPacketIdentifier());
		output.writeInt(buffer.size());
		byte[] result = buffer.toArray();
		output.write(result);
		output.flush();
		buffer.close();
		buffer = null;
		return result.length;
	}
	
	protected abstract void writePacketData(DataBuffer buffer) throws IOException;
	public abstract void readPacketData(DataInputStream input, int packetSize) throws IOException;
}

package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 3)
public class DisconnectPacket extends Packet {

	String disconnectReason;
	public DisconnectPacket(String disconnectReason) {
		this.disconnectReason = disconnectReason;
	}
	
	public DisconnectPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeUTF(disconnectReason);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		disconnectReason = input.readUTF();
	}

	public String getMessage() {
		return disconnectReason;
	}

}

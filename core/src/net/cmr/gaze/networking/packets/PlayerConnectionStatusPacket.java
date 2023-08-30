package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.ConnectionStatus;
import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 5)
public class PlayerConnectionStatusPacket extends Packet {
	
	String username;
	ConnectionStatus status;
	public PlayerConnectionStatusPacket(String username, ConnectionStatus status) {
		this.username = username;
		this.status = status;
	}
	
	public PlayerConnectionStatusPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public String getUsername() {
		return username;
	}
	public ConnectionStatus getStatus() {
		return status;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeUTF(username);
		buffer.writeInt(status.ordinal());
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		username = input.readUTF();
		status = ConnectionStatus.values()[input.readInt()];
	}

}

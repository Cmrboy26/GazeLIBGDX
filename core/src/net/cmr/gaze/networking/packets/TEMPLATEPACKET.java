package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 69420)
public class TEMPLATEPACKET extends Packet {

	public TEMPLATEPACKET(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public TEMPLATEPACKET() {
		
	}
	
	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		
	}
	
}

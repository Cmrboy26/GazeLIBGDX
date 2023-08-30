package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.inventory.Inventory;
import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 22)
public class ChestInventoryPacket extends Packet {

	public ChestInventoryPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	//Inventory inventory;
	int x, y;
	
	public ChestInventoryPacket(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeInt(x);
		buffer.writeInt(y);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		x = input.readInt();
		y = input.readInt();
	}
	
}

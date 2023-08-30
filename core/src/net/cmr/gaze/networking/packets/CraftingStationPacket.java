package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.crafting.Crafting.CraftingStation;
import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 19)
public class CraftingStationPacket extends Packet {

	int x, y;
	CraftingStation station;
	
	public CraftingStationPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public CraftingStationPacket(int x, int y, CraftingStation station) {
		this.x = x;
		this.y = y;
		this.station = station;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public CraftingStation getStation() {
		return station;
	}
	
	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeInt(station.ordinal());
		buffer.writeInt(x);
		buffer.writeInt(y);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		station = CraftingStation.values()[input.readInt()];
		x = input.readInt();
		y = input.readInt();
	}

}

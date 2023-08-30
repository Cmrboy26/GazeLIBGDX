package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.world.Tile;

@PacketID(id = 8)
public class TileUpdatePacket extends Packet {

	Tile tile;
	int x, y, layer;
	
	public TileUpdatePacket(Tile tile, int x, int y, int layer) {
		this.tile = tile;
		this.x = x;
		this.y = y;
		this.layer = layer;
	}
	
	public TileUpdatePacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getLayer() {
		return layer;
	}
	public Tile getTile() {
		return tile;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		Tile.writeOutgoingTile(tile, buffer);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(layer);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		tile = Tile.readIncomingTile(input);
		x = input.readInt();
		y = input.readInt();
		layer = input.readInt();
	}

}

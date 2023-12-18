package net.cmr.gaze.world;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.world.abstractTiles.MultiTile;

public class StructureTile extends Tile {

	int x, y;
	
	public StructureTile(TileType tileType, int x, int y) {
		super(tileType);
		this.x = x;
		this.y = y;
	}

	public TileType[] belowWhitelist() {
		return null;
	}

	public TileType[] belowBlacklist() {
		return null;
	}
	
	public Tile getMultiTileCore(World world, int tilex, int tiley) {
		return (MultiTile) world.getTile(tilex-x, tiley-y, getType().layer);
	}
	
	public Tile getMultiTileCore(TileData data, int tilex, int tiley) {
		return (MultiTile) data.getTile(tilex-x, tiley-y, getType().layer);
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		throw new NullPointerException("I dont know how you used this method but you did and you shouldn't have");
	}
	
	@Override
	public void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		buffer.writeInt(tile.ordinal());
		buffer.writeInt(x);
		buffer.writeInt(y);
	}

}

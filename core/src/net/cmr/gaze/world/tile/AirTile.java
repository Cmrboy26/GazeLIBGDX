package net.cmr.gaze.world.tile;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;

/** 
 * Should only be used in structure tiles
 */
public class AirTile extends Tile {

	public AirTile() {
		super(TileType.AIR);
	}

	@Override
	public TileType[] belowWhitelist() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TileType[] belowBlacklist() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		// TODO Auto-generated method stub
		
	}

}

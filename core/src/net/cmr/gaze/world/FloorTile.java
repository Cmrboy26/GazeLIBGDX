package net.cmr.gaze.world;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.world.entities.Player;

public abstract class FloorTile extends Tile {
	
	Tile underTile;
	
	public FloorTile(TileType tileType) {
		super(tileType);
	}
	
	public void readFloorTileData(DataInputStream input) throws IOException {
		underTile = Tile.readIncomingTile(input);
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		Tile.writeOutgoingTile(underTile, buffer);
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		world.addTile(underTile, x, y);
	}

	protected void renderBelowTile(Gaze game, HashMap<Point, Tile[][][]> chunks, int x, int y) {
		if(underTile!=null) {
			underTile.render(game, chunks, x, y);
		}
	}
	
	public void setUnderTile(Tile at) {
		this.underTile = at;
	}

	public Tile getUnderTile() {
		return underTile;
	}
	
}

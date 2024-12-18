package net.cmr.gaze.world.abstractTiles;

import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;

public abstract class MultiTile extends Tile {

	int width, height;
	
	public MultiTile(TileType tileType, int width, int height) {
		super(tileType);
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}

}

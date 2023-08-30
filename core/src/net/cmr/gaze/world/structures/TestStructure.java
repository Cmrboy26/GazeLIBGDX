package net.cmr.gaze.world.structures;

import java.awt.Point;

import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;

public class TestStructure extends Structure {

	public TestStructure(Point worldCoordinate) {
		super(worldCoordinate, 3, 2);
	}

	static Tile[][][] thing;
	
	@Override
	public Tile[][][] getTiles() {
		if(thing == null) {
			thing = new Tile[getWidth()][getHeight()][2];
			thing[0][0][0] = Tiles.getTile(TileType.SAND);
			thing[1][0][0] = Tiles.getTile(TileType.SAND);
			thing[2][0][0] = Tiles.getTile(TileType.SAND);
			thing[0][1][0] = Tiles.getTile(TileType.SAND);
			thing[1][1][0] = Tiles.getTile(TileType.SAND);
			thing[2][1][0] = Tiles.getTile(TileType.SAND);
			thing[0][1][1] = Tiles.getTile(TileType.FURNACE);
			thing[1][1][1] = Tiles.getTile(TileType.TORCH);
			thing[2][1][1] = Tiles.getTile(TileType.CHEST);
		}
		return thing;
	}

}

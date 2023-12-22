package net.cmr.gaze.world.structures;

import java.awt.Point;

import net.cmr.gaze.world.Chunk;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.tile.Air;
import net.cmr.gaze.world.tile.ChestTile;

public abstract class Structure {
	
	Point worldCoordinate;
	int width, height;
	
	public Structure(Point worldCoordinate, int width, int height) {
		this.worldCoordinate = worldCoordinate;
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	
	public abstract Tile[][][] getTiles();
	public void generateChestLoot(ChestTile tile, int x, int y) {}
	
	private static Structure largeTreeStructure;
	public static Structure multiTileToStructure(Point point, TileType type) {
		if(type==TileType.TREE) {
			if(largeTreeStructure == null) {
				largeTreeStructure = new Structure(point, 1, 1) {
					Tile[][][] thing;
					
					@Override
					public Tile[][][] getTiles() {
						if(thing == null) {
							thing = new Tile[getWidth()][getHeight()][2];
							thing[0][0][0] = Tiles.getTile(TileType.TREE);
							//thing[1][0][0] = new StructureTile(TileType.LargeTree, 1, 0);
						}
						return thing;
					}
				};
			}
			return largeTreeStructure;
		}
		return null;
	}
	public final void generateStructure(Chunk c) {
		Tile[][][] structureTiles = getTiles();
		Point structureChunk = Chunk.getChunk(worldCoordinate);
		Point relativePoint = Chunk.getInsideChunkCoordinates(worldCoordinate.x, worldCoordinate.y);
		int relativeX = (structureChunk.x-c.getCoordinate().x)*Chunk.CHUNK_SIZE+relativePoint.x;
		int relativeY = (structureChunk.y-c.getCoordinate().y)*Chunk.CHUNK_SIZE+relativePoint.y;
		for(int z = 0; z < structureTiles[0][0].length; z++) {
			for(int x = 0; x < structureTiles.length; x++) {
				for(int y = 0; y < structureTiles[0].length; y++) {
					Tile at = structureTiles[x][y][z];
					at = Tiles.getTile(at.getType());
					if(at == null && z == 0) {
						continue;
					}
					int endX = relativeX+x;
					int endY = relativeY+y;
					if(c.getCoordinate().equals(new Point(0,0))) {
						//System.out.println(endX+","+endY);
					}
					if(endX >= 0 && endX < Chunk.CHUNK_SIZE) {
						if(endY >= 0 && endY < Chunk.CHUNK_SIZE) {
							/*int layer = z;
							if(at != null) {
								layer = at.getType().layer;
							}*/
							Point p = c.relativeToWorldCoordinates(new Point(endX, endY));
							if(at != null) {
								if(at instanceof ChestTile) {
									generateChestLoot((ChestTile) at, endX, endY);
								}
								if(at instanceof Air) {
									at = null;
									c.getWorld().removeTile(p.x, p.y, z);
								} else {
									c.getWorld().removeTile(p.x, p.y, at.getType().layer);
									c.getWorld().generateTile(c, at, p.x, p.y);
								}
							}
							//c.getTiles()[endX][endY][layer] = at;
						}
					}
				}
			}
		}
	}

	public Point getWorldCoordinates() {
		return worldCoordinate;
	}
	
}

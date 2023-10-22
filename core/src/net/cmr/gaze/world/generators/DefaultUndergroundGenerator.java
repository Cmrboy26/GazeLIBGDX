package net.cmr.gaze.world.generators;

import java.awt.Point;

import net.cmr.gaze.util.SimplexNoise;
import net.cmr.gaze.world.Chunk;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.WorldGenerator;
import net.cmr.gaze.world.structures.ChestStructure;
import net.cmr.gaze.world.structures.Structure;

public class DefaultUndergroundGenerator extends WorldGenerator {

	@Override
	public void generate(Chunk chunk, int minX, int minY, double seed) {
		
		for(int x = minX; x < minX+Chunk.CHUNK_SIZE; x++) {
			for(int y = minY; y < minY+Chunk.CHUNK_SIZE; y++) {
				double noise = SimplexNoise.noise(3, 1/75d, 4, .5d, 2d, x, y, seed, .8d*seed);
				
				if(noise < -2.5) {
					generateTile(chunk, Tiles.getTile(TileType.WATER), x, y);
				} else if(noise < .6) {
					double grass = SimplexNoise.noise(1.5, 1/15d, 4, .5d, 2d, x/1.5, y/1.5, seed/2d, .8d*seed);
					if(grass > -.9) {
						generateTile(chunk, Tiles.getTile(TileType.STONE), x, y);
					} else {
						generateTile(chunk, Tiles.getTile(TileType.DIRT), x, y);
					}
				} else if(noise < .8){
					//generateTile(chunk, Tiles.getTile(TileType.StoneTile), x, y);
					generateTile(chunk, Tiles.getTile(TileType.STONE), x, y);
				} else {
					generateTile(chunk, Tiles.getTile(TileType.LAVA), x, y);
				} 
			}
		}
		
		for(int x = minX; x < minX+Chunk.CHUNK_SIZE; x++) {
			for(int y = minY; y < minY+Chunk.CHUNK_SIZE; y++) {
				double noise = SimplexNoise.noise(1, 1, 10, .5, 2, x/13f, y/13f, seed/2);
				double grassSpace = SimplexNoise.noise(1.5, 1/25d, 4, .5d, 2d, x/1.5, y/1.5, seed*1.5d, seed/5);
				if (grassSpace < .5) {
					// ore gen
					if (noise > -.75d) {
						
						double ironNoise = SimplexNoise.noise(1, 1, 10, .5, 2, x/7f, y/7f, seed/9, seed/1.1);
						double copperNoise = SimplexNoise.noise(1, 1, 10, .5, 2, x/7f, y/7f, seed/2, seed/3.1);
						
						if(ironNoise > .5) {
							generateTile(chunk, Tiles.getTile(TileType.IRON_ORE_WALL), x, y);
						} 
						else if(copperNoise > .5) {
							generateTile(chunk, Tiles.getTile(TileType.COPPER_ORE_WALL), x, y);
						}
						else generateTile(chunk, Tiles.getTile(TileType.STONE_WALL), x, y);
					}
				} else {
					if (noise < .25) {
						generateTile(chunk, Tiles.getTile(TileType.TALL_GRASS), x, y);
					}
				}
			}
		}
		chunk.generated = true;
	}

	public WorldGeneratorType getGeneratorType() {
		return WorldGeneratorType.DEFAULT_UNDERGROUND;
	}

	@Override
	public void prepareDecorationGeneration(Point chunk, double seed) {
		for(int x = chunk.x*Chunk.CHUNK_SIZE; x < chunk.x*Chunk.CHUNK_SIZE+Chunk.CHUNK_SIZE; x++) {
			for(int y = chunk.y*Chunk.CHUNK_SIZE; y < chunk.y*Chunk.CHUNK_SIZE+Chunk.CHUNK_SIZE; y++) {
				Point world = new Point(x, y);
				if(Math.abs(x)%24==0 && Math.abs(y)%24==0) {
					Structure structure = new ChestStructure(world);
					addStructure(structure);
				}
			}
		}
	}
	
}

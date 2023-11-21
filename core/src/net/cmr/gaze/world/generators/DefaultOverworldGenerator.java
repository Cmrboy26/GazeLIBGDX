package net.cmr.gaze.world.generators;

import java.awt.Point;

import net.cmr.gaze.util.SimplexNoise;
import net.cmr.gaze.world.Chunk;
import net.cmr.gaze.world.EnvironmentController;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.WorldGenerator;
import net.cmr.gaze.world.EnvironmentController.EnvironmentControllerType;
import net.cmr.gaze.world.structures.ChestStructure;
import net.cmr.gaze.world.structures.Structure;
import net.cmr.gaze.world.tile.DirtTile;

public class DefaultOverworldGenerator extends WorldGenerator {

	@Override
	public void generate(Chunk chunk, int minX, int minY, double seed) {
		
		for(int x = minX; x < minX+Chunk.CHUNK_SIZE; x++) {
			for(int y = minY; y < minY+Chunk.CHUNK_SIZE; y++) {
				double noise = SimplexNoise.noise(3, 1/75d, 4, .5d, 2d, x, y, seed, .8d*seed);
				if(noise < .6) {
					double grass = SimplexNoise.noise(1.5, 1/15d, 4, .5d, 2d, x/1.5, y/1.5, seed/2d, .8d*seed);
					if(grass > -.9) {
						generateTile(chunk, Tiles.getTile(TileType.GRASS), x, y);
					} else {
						DirtTile tile = new DirtTile();
						tile.setPersistence(true);
						generateTile(chunk, tile, x, y);
					}
				} else if(noise < .8){
					generateTile(chunk, Tiles.getTile(TileType.SAND), x, y);
				} else {
					generateTile(chunk, Tiles.getTile(TileType.WATER), x, y);
				}
			}
		}
		
		for(int x = minX; x < minX+Chunk.CHUNK_SIZE; x++) {
			for(int y = minY; y < minY+Chunk.CHUNK_SIZE; y++) {
				double noise = SimplexNoise.noise(x*100, y*100, seed);
				double grassSpace = SimplexNoise.noise(1.5, 1/25d, 4, .5d, 2d, x/1.5, y/1.5, seed*1.5d, seed/5);
				if (grassSpace < .5) {
					if (noise < -.1d) {
						if(noise > -.2d) {
							generateTile(chunk, Tiles.getTile(TileType.TREE_SAPLING), x, y);
						} else {
							generateTile(chunk, Tiles.getTile(TileType.TREE), x, y);
						}
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
	
	@Override
	public void prepareDecorationGeneration(Point chunk, double seed) {
		/*for(int x = chunk.x*Chunk.CHUNK_SIZE; x < chunk.x*Chunk.CHUNK_SIZE+Chunk.CHUNK_SIZE; x++) {
			for(int y = chunk.y*Chunk.CHUNK_SIZE; y < chunk.y*Chunk.CHUNK_SIZE+Chunk.CHUNK_SIZE; y++) {
				Point world = new Point(x, y);
				if(Math.abs(x)%24==0 && Math.abs(y)%24==0) {
					Structure structure = new ChestStructure(world);
					addStructure(structure);
				}
			}
		}*/
	}

	public WorldGeneratorType getGeneratorType() {
		return WorldGeneratorType.DEFAULT_OVERWORLD;
	}

	@Override
	public EnvironmentControllerType getEnvironmentControllerType() {
		return EnvironmentControllerType.DEFAULT;
	}
	
}

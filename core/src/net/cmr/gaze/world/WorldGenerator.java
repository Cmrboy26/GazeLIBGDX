package net.cmr.gaze.world;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import net.cmr.gaze.util.Normalize;
import net.cmr.gaze.world.EnvironmentController.EnvironmentControllerType;
import net.cmr.gaze.world.generators.DefaultOverworldGenerator;
import net.cmr.gaze.world.generators.DefaultUndergroundGenerator;
import net.cmr.gaze.world.structures.Structure;

public abstract class WorldGenerator {
	
	public enum WorldGeneratorType {
		
		// Underground versions of a generatorType should be negative
		
		DEFAULT_OVERWORLD(1),
		DEFAULT_UNDERGROUND(-1);
		
		int id;
		
		WorldGeneratorType(int id) {
			this.id = id;
		}
		
		public int getID() {
			return id;
		}
		
		public static WorldGeneratorType getTypeFromID(int id) {
			for(WorldGeneratorType type : WorldGeneratorType.values()) {
				if(type.getID()==id) {
					return type;
				}
			}
			return null;
		}
		
		/**
		 * @return The underground or above ground generator of the specified generator.
		 * Returns null if there is not a corresponding underground generator for the specified generator.
		 */
		public WorldGeneratorType invertUnderground() {
			int neededID = getID()*-1;
			for(WorldGeneratorType type : WorldGeneratorType.values()) {
				if(type.getID()==neededID) {
					return type;
				}
			}
			return null;
		}

		public boolean isUnderground() {
			return Normalize.norm(id)==-1;
		}
		
	}
	
	public static WorldGenerator getGenerator(WorldGeneratorType type) {
		switch(type) {
		case DEFAULT_OVERWORLD:
			return new DefaultOverworldGenerator();
		case DEFAULT_UNDERGROUND:
			return new DefaultUndergroundGenerator();
		default:
			break;
		}
		return null;
	}
	
	HashMap<Point, LinkedList<Structure>> chunkStructures;
	HashSet<Point> chunkStructuresGenerated;
	
	public WorldGenerator() {
		chunkStructures = new HashMap<>();
		chunkStructuresGenerated = new HashSet<>();
	}
	
	public void prepareDecorationGeneration(Chunk inputChunk, double seed) {
		int area = 3;
		for(int x = 0; x<area; x++) {
			for(int y = 0; y<area; y++) {
				Point chunk = new Point(inputChunk.getCoordinate().x-x, inputChunk.getCoordinate().y-y);
				if(!chunkStructuresGenerated.contains(chunk)) {
					// generate needed structures using the noise function
					//addStructureToLists(new ChestStructure(new Point((chunk.x*Chunk.CHUNK_SIZE)+13, (chunk.y*Chunk.CHUNK_SIZE))));
					prepareDecorationGeneration(chunk, seed);
					chunkStructuresGenerated.add(chunk);
				}
			}
		}
	}
	
	public abstract void prepareDecorationGeneration(Point chunk, double seed);
	
	public void addStructure(Structure structure) {
		Point leftBottomChunk = Chunk.getChunk(structure.getWorldCoordinates());
		Point rightTopChunk = Chunk.getChunk(structure.getWorldCoordinates().x+structure.getWidth(), structure.getWorldCoordinates().y+structure.getHeight());
		
		for(int x = leftBottomChunk.x; x<=rightTopChunk.x; x++) {
			for(int y = leftBottomChunk.y; y<=rightTopChunk.y; y++) {
				Point chunk = new Point(x, y);
				if(!chunkStructures.containsKey(chunk)) {
					chunkStructures.put(chunk, new LinkedList<>());
				}
				LinkedList<Structure> list = chunkStructures.get(chunk);
				list.add(structure);
			}
		}
	}
	
	public void generateStructures(Chunk chunk) {
		LinkedList<Structure> list = chunkStructures.get(chunk.getCoordinate());
		if(list==null) {
			return;
		}
		for(Structure structure : list) {
			structure.generateStructure(chunk);
		}
	}
	
	public WorldGenerator invertUnderground() {
		return getGenerator(getGeneratorType().invertUnderground());
	}
	
	public void generate(Chunk chunk) {
		prepareDecorationGeneration(chunk, chunk.world.getSeed());
		generate(chunk, chunk.getCoordinate().x*Chunk.CHUNK_SIZE, chunk.getCoordinate().y*Chunk.CHUNK_SIZE, chunk.world.getSeed());
		generateStructures(chunk);
		chunk.generated = true;
	}
	
	public void generateTile(Chunk chunk, Tile tile, int x, int y) {
		chunk.world.generateTile(chunk, tile, x, y);
	}
	
	protected abstract void generate(Chunk chunk, int minX, int minY, double seed);
	public abstract WorldGeneratorType getGeneratorType();

	public boolean isUnderground() {
		return Normalize.norm(getGeneratorType().id)==-1;
	}

	public abstract EnvironmentControllerType getEnvironmentControllerType();
	public final EnvironmentController getEnvironmentController(double seed) {
		return EnvironmentController.getEnvironmentController(getEnvironmentControllerType(), seed);
	}
	
	
}

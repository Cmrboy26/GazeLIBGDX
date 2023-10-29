package net.cmr.gaze.world;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.util.Pair;
import net.cmr.gaze.world.TileType.TickType;
import net.cmr.gaze.world.abstractTiles.ElectricityPole;
import net.cmr.gaze.world.entities.Entity;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;

public class Chunk {

	public static final int CHUNK_SIZE = 16;
	public static final int LAYERS = 3;
	
	public boolean generated = false;
	World world = null;
	private Point chunkCoordinate = null;
	private ArrayList<Entity> entities = null;

	/**
	 * The point stored in the Pair contains the tile's WORLD coordinates, not CHUNK coordinates
	 */
	private ConcurrentHashMap<Pair<Point, Integer>, Tile> updateTiles, constantUpdateTiles;
	private Tile[][][] tileData = null;
	
	private ArrayList<Entity> removeList;
	
	public Chunk(World world, Point chunkCoordinate) {
		this.world = world;
		this.updateTiles = new ConcurrentHashMap<>();
		this.constantUpdateTiles = new ConcurrentHashMap<>();
		this.chunkCoordinate = chunkCoordinate;
		this.tileData = new Tile[CHUNK_SIZE][CHUNK_SIZE][LAYERS];
		this.entities = new ArrayList<>();
		this.removeList = new ArrayList<>();
	}
	
	private Chunk() {
		this.updateTiles = new ConcurrentHashMap<>();
		this.constantUpdateTiles = new ConcurrentHashMap<>();
		this.tileData = new Tile[CHUNK_SIZE][CHUNK_SIZE][LAYERS];
		this.entities = new ArrayList<>();
		this.removeList = new ArrayList<>();
	}
	
	public boolean isGenerated() {
		return generated;
	}
	
	public void generate() {
		world.getGenerator().generate(this);
		/*
		int minX = chunkCoordinate.x*Chunk.CHUNK_SIZE;
		int minY = chunkCoordinate.y*Chunk.CHUNK_SIZE;
		if(chunkCoordinate.x > 500) {
			// ? idk why this was here
			//throw new NullPointerException();
		}
		
		for(int x = minX; x < minX+CHUNK_SIZE; x++) {
			for(int y = minY; y < minY+CHUNK_SIZE; y++) {
				double noise = SimplexNoise.noise(1, 1/25d, 2, .5d, 2d, x, y, world.getSeed(), .8d*world.getSeed());
				if(noise < .4) {
					world.generateTile(this, Tiles.getTile(TileType.Grass), x, y);
				} else if(noise < .6){
					world.generateTile(this, Tiles.getTile(TileType.Sand), x, y);
				} else {
					world.generateTile(this, Tiles.getTile(TileType.Water), x, y);
				}
			}
		}
		
		
		
		for(int x = minX; x < minX+CHUNK_SIZE; x++) {
			for(int y = minY; y < minY+CHUNK_SIZE; y++) {
				double noise = SimplexNoise.noise(x*100, y*100, world.getSeed());
				Point p = new Point(x, y);
				if(noise < -.1d) {
					world.generateTile(this, Tiles.getTile(TileType.LargeTree), x, y);
				}
			}
		}
		generated = true;*/
	}
	
	/**
	 * This method will be called once every 1/60 of a second.
	 * @param loadedByPlayer will be true if a player is within simulation distance of the chunk
	 * (Simulation distance is set in World.SIMULATION_DISTANCE
	 * @param updateTiles will be true once every 1/20 of a second.
	 * Tile updates should occur when this value is true
	 */
	
	public void update(boolean loadedByPlayer, boolean updateTiles) {
		if(updateTiles) {
			for(Pair<Point, Integer> point : constantUpdateTiles.keySet()) {
				constantUpdateTiles.get(point).update(world.tileData, point.getFirst());
				//System.out.println("UPDATED CONSTANT: "+constantUpdateTiles.get(point));
			}
			if(loadedByPlayer) {
				for(Pair<Point, Integer> point : this.updateTiles.keySet()) {
					this.updateTiles.get(point).update(world.tileData, point.getFirst());
					//System.out.println("UPDATED NEARBY: "+this.updateTiles.get(point));
				}
			}
		}
		if(loadedByPlayer) {
			for(Entity entity : entities) {
				entity.update(Entity.DELTA_TIME, world.tileData);
				if(!entity.getChunk().equals(chunkCoordinate)) {
					world.updateEntityChunk(entity);
					removeList.add(entity);
				}
			}
			while(removeList.size() > 0) {
				entities.remove(removeList.get(0));
				removeList.remove(0);
			}
		}
	}
	
	public Tile getTile(Point coordinates, int layer) {
		return tileData[coordinates.x][coordinates.y][layer];
	}
	
	public Tile[][][] getTiles() {
		return tileData;
	}
	public Point getCoordinate() {
		return chunkCoordinate;
	}
	
	public void setTile(Tile t, Point coordinates, int layer) {
		Tile at = getTile(coordinates, layer);
		tileData[coordinates.x][coordinates.y][layer] = t;
		Point worldCoord = relativeToWorldCoordinates(coordinates);
		
		constantUpdateTiles.remove(new Pair<>(worldCoord, layer));
		updateTiles.remove(new Pair<>(worldCoord, layer));
		
		if(at != null && !(at instanceof StructureTile)) {
			if(at.getType().type == TickType.CONSTANT) {
				constantUpdateTiles.remove(new Pair<>(worldCoord, layer));
			}
			if(at.getType().type == TickType.NEARBY) {
				updateTiles.remove(new Pair<>(worldCoord, layer));
			}
		}
		
		if(t != null && !(t instanceof StructureTile)) {
			if(t.getType().type == TickType.CONSTANT) {
				constantUpdateTiles.put(new Pair<>(worldCoord, layer), t);
			}
			if(t.getType().type == TickType.NEARBY) {
				updateTiles.put(new Pair<>(worldCoord, layer), t);
			}
		}
	}
	
	public static boolean areChunksWithinRange(Point cnk1, Point cnk2, int range) {
		return Math.abs(cnk1.x-cnk2.x)<=range && Math.abs(cnk1.y-cnk2.y)<=range;
	}
	
	public static Point getChunk(Point tileCoordinates) {
		return new Point((int) Math.floor((double) tileCoordinates.x/Chunk.CHUNK_SIZE), (int) Math.floor((double) tileCoordinates.y/Chunk.CHUNK_SIZE));
	}
	public static Point getChunk(int x, int y) {
		return new Point((int) Math.floor((double) x/Chunk.CHUNK_SIZE), (int) Math.floor((double) y/Chunk.CHUNK_SIZE));
	}
	public Point relativeToWorldCoordinates(Point relativeCoordinates) {
		return new Point(relativeCoordinates.x+chunkCoordinate.x*Chunk.CHUNK_SIZE, relativeCoordinates.y+chunkCoordinate.y*Chunk.CHUNK_SIZE);
	}
	public static Point relativeToWorldCoordinates(Point chunkCoordinates, Point relativeCoordinates) {
		return new Point(relativeCoordinates.x+chunkCoordinates.x*Chunk.CHUNK_SIZE, relativeCoordinates.y+chunkCoordinates.y*Chunk.CHUNK_SIZE);
	}
	@Deprecated
	public static Point getInsideChunkCoordinates(Point tileCoordinates) {
		return new Point(Math.floorMod(tileCoordinates.x,Chunk.CHUNK_SIZE), Math.floorMod(tileCoordinates.y,Chunk.CHUNK_SIZE));
	}
	public static Point getInsideChunkCoordinates(int x, int y) {
		return new Point(Math.floorMod(x,Chunk.CHUNK_SIZE), Math.floorMod(y,Chunk.CHUNK_SIZE));
	}
	
	public void addEntity(Entity entity) {
		entities.add(entity);
	}
	public void removeEntity(Entity entity) {
		removeList.add(entity);
	}
	
	public ArrayList<Entity> getEntities() {
		return entities;
	}
	
	// NOTE: this should only be used for reading/writing to file, not for sending data over to the client.
		// (because of Entity.writeEntity(buffer, false, TRUE) where true means that it is writing to FILE)
	public void writeChunk(DataBuffer buffer, DataBuffer electricityDistributors) throws IOException {
		buffer.writeInt(chunkCoordinate.x);
		buffer.writeInt(chunkCoordinate.y);
		buffer.writeBoolean(generated);
		for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
				for(int z = 0; z < Chunk.LAYERS; z++) {
					Tile toAdd = tileData[x][y][z];
					Tile.writeOutgoingTile(toAdd, buffer);
					if(toAdd instanceof ElectricityPole) {
						((ElectricityPole) toAdd).writeConnections(electricityDistributors);
					}
				}
			}
		}
		buffer.writeInt(entities.size());
		for(int i = 0; i < entities.size(); i++) {
			entities.get(i).writeEntity(buffer, false, true);
		}
	}
	
	// NOTE: this should only be used for reading/writing to file, not for sending data over to the client.
	// (because of Entity.readEntity(input, TRUE) where true means that it is reading from FILE)
	public static Chunk readChunk(DataInputStream input, World world) throws IOException {
		Chunk end = new Chunk();
		end.world = world;
		end.chunkCoordinate = new Point(input.readInt(), input.readInt());
		end.generated = input.readBoolean();
		for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
				for(int z = 0; z < Chunk.LAYERS; z++) {
					Tile in = Tile.readIncomingTile(input);
					end.tileData[x][y][z] = in;
					
					if(in != null && !(in instanceof StructureTile)) {
						Point worldCoord = end.relativeToWorldCoordinates(new Point(x, y));
						if(in.getType().type == TickType.CONSTANT) {
							end.constantUpdateTiles.put(new Pair<>(worldCoord, z), in);
						}
						if(in.getType().type == TickType.NEARBY) {
							end.updateTiles.put(new Pair<>(worldCoord, z), in);
						}
					}
					
				}
			}
		}
		end.entities = new ArrayList<>();
		int entityLength = input.readInt();
		for(int i = 0; i < entityLength; i++) {
			Entity entity = Entity.readEntity(input, true);
			entity.setWorld(world);
			end.entities.add(entity);
		}
		return end;
	}

	public World getWorld() {
		return world;
	}
	
}

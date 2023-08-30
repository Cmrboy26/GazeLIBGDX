package net.cmr.gaze.world;

import java.awt.Point;
import java.util.HashMap;

// A "wrapper" object that 
public class TileData {

	private World serverData;
	private HashMap<Point, Tile[][][]> clientData;
	
	public TileData(World serverData) {
		this.serverData = serverData;
	}
	public TileData(HashMap<Point, Tile[][][]> clientData) {
		this.clientData = clientData;
	}
	
	public boolean isClient() {
		return clientData!=null;
	}
	public boolean isServer() {
		return serverData!=null;
	}
	
	public Tile getTile(int x, int y, int z) {
		if(isClient()) {
			Point chunk = Chunk.getChunk(x, y);
			Tile[][][] data = clientData.get(chunk);
			if(data != null) {
				Point relative = Chunk.getInsideChunkCoordinates(x, y);
				return data[relative.x][relative.y][z];
			}
		}
		if(isServer()) {
			return serverData.getTile(x, y, z);
		}
		return null;
	}
	
	public Tile[] getAdjacents(int x, int y, int z) {
		Tile[] end = new Tile[9];
		if(isServer()) {
			for(int ty = 1; ty >= -1; ty--) {
				for(int tx = -1; tx<=1; tx++) {
					Tile at = getTile(tx+x, ty+y, z);
					end[((ty*-1)+1)*3+(tx+1)] = at;
				}
			}
		}
		if(isClient()) {
			
		}
		
		return end;
	}
	
	public void addTile(Tile tile, int x, int y) {
		if(isClient()) {
			Point chunk = Chunk.getChunk(x, y);
			Tile[][][] data = clientData.get(chunk);
			if(data != null) {
				Point relative = Chunk.getInsideChunkCoordinates(x, y);
				data[relative.x][relative.y][tile.getType().layer] = tile;
			}
		}
		if(isServer()) {
			serverData.addTile(tile, x, y);
		}
	}
	
	public World getServerData() {
		if(isServer()) {
			return serverData;
		} else {
			throw new IllegalStateException("Development problem: Attempted to access world object on client side. (Did you forget a isServer() check before accessing?)");
		}
	}
	
}

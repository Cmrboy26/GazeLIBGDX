package net.cmr.gaze.world;

import java.awt.Point;
import java.util.HashMap;

import net.cmr.gaze.stage.GameScreen;

// A "wrapper" object that 
public class TileData {

	private World serverData;
	private HashMap<Point, Tile[][][]> clientData;
	private GameScreen screen;
	
	public TileData(World serverData) {
		this.serverData = serverData;
	}
	public TileData(GameScreen screen) {
		this.screen = screen;
		this.clientData = screen.getTiles();
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

	public EnvironmentController getEnvironmentController() {
		if(isServer()) {
			return serverData.getEnvironmentController();
		} else {
			return screen.getEnvironmentController();
		}
	}
	
	public World getServerData() {
		if(isServer()) {
			return serverData;
		} else {
			throw new IllegalStateException("Development problem: Attempted to access world object on client side. (Did you forget a isServer() check before accessing?)");
		}
	}
    public HashMap<Point, Tile[][][]> getClientData() {
        if(isClient()) {
			return clientData;
		} else {
			throw new IllegalStateException("Development problem: Attempted to access world object on server side. (Did you forget a isClient() check before accessing?)");
		}
    }
	public GameScreen getScreen() {
		if(isClient()) {
			return screen;
		} else {
			throw new IllegalStateException("Development problem: Attempted to access screen object on server side. (Did you forget a isClient() check before accessing?)");
		}
	}
	
}

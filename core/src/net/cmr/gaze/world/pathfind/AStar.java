package net.cmr.gaze.world.pathfind;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import net.cmr.gaze.util.ArrayUtil;
import net.cmr.gaze.world.Chunk;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.entities.Entity;

public class AStar {
	
	public static Vector2 findMovementVector(Entity entity, TileData data, int chunkRadius, Point goalTile) {
		Point centerChunk = entity.getChunk();
		int dim = Chunk.CHUNK_SIZE*(chunkRadius*2+1);
		int[][] map = new int[dim][dim];
		for(int chunkY = -chunkRadius; chunkY<=chunkRadius; chunkY++) {
			for(int chunkX = -chunkRadius; chunkX <= chunkRadius; chunkX++) {
				Point chunk = new Point(centerChunk);
				chunk.translate(chunkX, chunkY);
				int offsetX = chunk.x*Chunk.CHUNK_SIZE;
				int offsetY = chunk.y*Chunk.CHUNK_SIZE;
				
				int arrayOffsetX = (chunkX+chunkRadius)*Chunk.CHUNK_SIZE;
				int arrayOffsetY = (chunkY+chunkRadius)*Chunk.CHUNK_SIZE;
				
				for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
					for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
						
						Tile tile = data.getTile(x+offsetX, y+offsetY, 1);
						map[x+arrayOffsetX][y+arrayOffsetY] = (tile==null || tile.getBoundingBox(x, y)==null?0:tile.getType().ordinal());
						
					}
				}
				
			}
		}
		
		//return new Vector2();
		AStarNode start = new AStarNode(Math.abs(entity.getTileX()-((centerChunk.x+chunkRadius)*Chunk.CHUNK_SIZE)), Math.abs(entity.getTileY()-((centerChunk.y+chunkRadius)*Chunk.CHUNK_SIZE)), null);
		AStarNode goal = new AStarNode(Math.abs(goalTile.x-((centerChunk.x+chunkRadius)*Chunk.CHUNK_SIZE)), Math.abs(goalTile.y-((centerChunk.y+chunkRadius)*Chunk.CHUNK_SIZE)), null);
		System.out.println(start);
		System.out.println(goal);
		
		return findMovementVector(map, start, goal);
	}
	
	public static Vector2 findMovementVector(int[][] walls, AStarNode start, AStarNode goal) {
		List<AStarNode> result = findPath(walls, start, goal);
		
		if(result != null) {
			AStarNode node = result.get(0);
			Vector2 vector = new Vector2(node.x-start.x, node.y-start.y);
			return vector;
		}
		
		return new Vector2(0, 0);
	}
	
	public static List<AStarNode> findPath(int[][] walls, AStarNode start, AStarNode goal) {
		boolean finished = false;
		ArrayList<AStarNode> checked = new ArrayList<>();
		checked.add(start);
		
		if(!isValid(walls, goal)) {
			return null;
		}
		if(!isValid(walls, start)) {
			return null;
		}
		
		while(!finished) {
			ArrayList<AStarNode> tempOpen = new ArrayList<>();
			for(int i = 0; i < checked.size(); i++) {
				AStarNode node = checked.get(i);
				for(AStarNode neighbor : findNeighbors(walls, node)) {
					if(!checked.contains(neighbor) && !tempOpen.contains(neighbor)) {
						tempOpen.add(neighbor);
					}
				}
			}
			for(AStarNode node : tempOpen) {
				checked.add(node);
				if(goal.equals(node)) {
					finished = true;
					break;
				}
			}
			if(!finished && tempOpen.isEmpty()) {
				return null;
			}
		}
		ArrayList<AStarNode> path = new ArrayList<>();
		AStarNode node = checked.get(checked.size()-1);
		while(node.parent != null) {
			path.add(0, node);
			node = node.parent;
		}
		return path;
	}
	
	public static List<AStarNode> findNeighbors(int[][] walls, AStarNode node) {
		ArrayList<AStarNode> neighbors = new ArrayList<>();
		
		AStarNode up = node.offset(0, 1);
		AStarNode down = node.offset(0, -1);
		AStarNode left = node.offset(-1, 0);
		AStarNode right = node.offset(1, 0);
		if(isValid(walls, up)) neighbors.add(up);
		if(isValid(walls, down)) neighbors.add(down);
		if(isValid(walls, left)) neighbors.add(left);
		if(isValid(walls, right)) neighbors.add(right);
		return neighbors;
	}
	
	public static boolean isValid(int[][] walls, AStarNode node) {
		if (node.y < 0 || node.y > walls.length - 1) return false;
        if (node.x < 0 || node.x > walls[0].length - 1) return false;
        return walls[node.y][node.x] == 0;
	}
	
	public static int getWidth(Object[][] array) {
		return array.length;
	}
	public static int getHeight(Object[][] array) {
		return array[0].length;
	}
	
}

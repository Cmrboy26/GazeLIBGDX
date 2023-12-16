package net.cmr.gaze.world.abstractTiles;

import java.awt.Point;
import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Sprite;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.util.CustomTime;
import net.cmr.gaze.world.Chunk;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;

public abstract class TransitionTile extends Tile {

	public TransitionTile(TileType tileType) {
		super(tileType);
	}

	public abstract String[] getTransitionSprite();
	public abstract TileType[] getTransitionTiles();

	public boolean transitionAllExcludeSelf() {
		return false;
	}
	
	public int[] typeIndex;
	public long lastCheck;
	Sprite[] transitionSprites = new Sprite[4];
	
	public void render(Gaze game, GameScreen screen, int x, int y) {
		
		if (CustomTime.timeToSeconds(System.nanoTime() - lastCheck) > .4) {
			updateSprites(game, screen, x, y);
		}
		
		for(int i = 0; i < 4; i++) {
			Sprite sprite = transitionSprites[i];
			if(sprite !=null) {
				game.batch.draw(sprite, x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE/2, TILE_SIZE/2, TILE_SIZE, TILE_SIZE, 1, 1, 90*i);
			}
		}
		
		super.render(game, screen, x, y);
	}
	
	public void updateSprites(Gaze game, GameScreen screen, int x, int y) {

		if(getTransitionSprite()==null) {
			return;
		}
		if(getTransitionTiles()==null) {
			return;
		}

		for (int v = 0; v < 4; v++) {
			
			int tx = 0, ty = 0;
			
			switch (v) {
			case(0): {
				tx = 1;
				ty = 0;
				break;
			}
			case(1): {
				tx = 0;
				ty = 1;
				break;
			}
			case(2): {
				tx = -1;
				ty = 0;
				break;
			}
			case(3): {
				tx = 0;
				ty = -1;
				break;
			}
			}
			
			Point relative = Chunk.getInsideChunkCoordinates(x+tx, y+ty);

			Tile[][][] chunk = screen.tileDataObject.getClientData().get(Chunk.getChunk(x+tx, y+ty));
			if (chunk == null) {
				continue;
			}

			Tile at = chunk[relative.x][relative.y][getType().layer];

			if (at == null) {
				continue;
			}

			
			boolean set = false;
			if(transitionAllExcludeSelf()) {
				if(at!=null && at.getType()!=getType()) {
					transitionSprites[v] = game.getSprite(getTransitionSprite()[0]);
					set = true;
				}
			} else {
				for (int i = 0; i < getTransitionTiles().length; i++) {
					TileType type = getTransitionTiles()[i];
					TileType atType = at.getType();
					if(at instanceof FloorTile && ((FloorTile)at).getTransitionSprite()==null) {
						Tile under = ((FloorTile)at).getUnderTile();
						if(under!=null) {
							atType=under.getType();
						}
					}
					if (type == atType) {
						transitionSprites[v] = game.getSprite(getTransitionSprite()[i]);
						set = true;
						break;
					}
				}
			}
			if(!set) {
				transitionSprites[v] = null;
			}
		}
		lastCheck = System.nanoTime();
	}
	
}

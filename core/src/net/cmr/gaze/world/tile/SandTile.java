package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TransitionTile;

public class SandTile extends TransitionTile {

	public SandTile() {
		super(TileType.SAND);
	}

	@Override
	public TileType[] belowWhitelist() {
		return null;
	}

	@Override
	public TileType[] belowBlacklist() {
		return null;
	}

	@Override
	public void render(Gaze game, HashMap<Point, Tile[][][]> chunks, int x, int y) {
		draw(game.batch, game.getSprite("sand"+getRandomizedInt(2, x, y)), x, y, 1, 1);
		//game.batch.draw(game.getSprite("sand"+getRandomizedInt(2, x, y)), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
		super.render(game, chunks, x, y);
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		return new SandTile();
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}

	
	final String[] transitionSprite = new String[] {"grassTransition", "waterTransition"};
	@Override
	public String[] getTransitionSprite() {
		return transitionSprite;
	}

	final TileType[] transitionTiles = new TileType[] {TileType.GRASS, TileType.WATER};
	@Override
	public TileType[] getTransitionTiles() {
		return transitionTiles;
	}

}

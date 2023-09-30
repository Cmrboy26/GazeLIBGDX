package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.SpeedChangeTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TransitionTile;

public class StoneTile extends TransitionTile implements SpeedChangeTile {

	public StoneTile() {
		super(TileType.STONE);
	}

	@Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		draw(game.batch, game.getSprite("stoneTile"), x, y, 1, 1);
		//game.batch.draw(game.getSprite("stoneTile"), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
		super.render(game, screen, x, y);
	}
	
	@Override
	public TileType[] belowWhitelist() {
		return null;
	}

	@Override
	public TileType[] belowBlacklist() {
		return null;
	}
	
	final String[] transitionSprite = new String[] {"dirtTransition", "lavaTransition"};
	@Override
	public String[] getTransitionSprite() {
		return transitionSprite;
	}

	final TileType[] transitionTiles = new TileType[] {TileType.DIRT, TileType.LAVA};
	@Override
	public TileType[] getTransitionTiles() {
		return transitionTiles;
	}

	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		return new StoneTile();
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}

	@Override
	public float getSpeedMultiplier() {
		return 1;
	}

}

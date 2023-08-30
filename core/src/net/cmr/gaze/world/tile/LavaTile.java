package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.LightSource;
import net.cmr.gaze.world.SpeedChangeTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TransitionTile;

public class LavaTile extends TransitionTile implements SpeedChangeTile, LightSource {

	public LavaTile() {
		super(TileType.LAVA);
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
		draw(game.batch, game.getAnimation("lava").getKeyFrame(Tile.tileRenderDelta, true), x, y, 1, 1);
		//game.batch.draw(game.getAnimation("lava").getKeyFrame(Tile.tileRenderDelta, true), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
		super.render(game, chunks, x, y);
	}

	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		return new LavaTile();
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}
	
	@Override
	public String getAmbientNoise(GameScreen game) {
		return "water"+new Random(this.hashCode()).nextInt(2);
	}

	final String[] transitionSprite = new String[] {"dirtTransition"};
	@Override
	public String[] getTransitionSprite() {
		return transitionSprite;
	}

	final TileType[] transitionTiles = new TileType[] {TileType.DIRT};
	@Override
	public TileType[] getTransitionTiles() {
		return transitionTiles;
	}
	
	@Override
	public float getSpeedMultiplier() {
		return .5f;
	}

	@Override
	public float getIntensity() {
		return 4;
	}

}

package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.TransitionTile;

public class SandstoneTile extends TransitionTile {

	public SandstoneTile() {
		super(TileType.SANDSTONE);
	}

	@Override
	public TileType[] belowWhitelist() {
		return null;
	}

	@Override
	public TileType[] belowBlacklist() {
		return null;
	}

    double lastCheck = 0;
	
	@Override
	public void update(TileData data, Point worldCoordinates) {
		if(data.isServer()) {
			lastCheck+=Tile.DELTA_TIME;
			if(lastCheck>5) {
				float chance = .01f;
				
				if(Math.random()<chance) {
					data.addTile(Tiles.getTile(TileType.SAND), worldCoordinates.x, worldCoordinates.y);
				}
				
				lastCheck = 0;
			}
		}
	}

	@Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		draw(game.batch, game.getSprite("sandstone"), x, y, 1, 1);
		super.render(game, screen, x, y);
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		return new SandstoneTile();
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}
	
	final String[] transitionSprite = new String[] {"grassTransition", "dirtTransition", "waterTransition", "sandTransition"};
	@Override
	public String[] getTransitionSprite() {
		return transitionSprite;
	}

	final TileType[] transitionTiles = new TileType[] {TileType.GRASS, TileType.DIRT, TileType.WATER, TileType.SAND};
	@Override
	public TileType[] getTransitionTiles() {
		return transitionTiles;
	}

}

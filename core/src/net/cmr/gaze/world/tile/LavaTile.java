package net.cmr.gaze.world.tile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.LightSource;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.abstractTiles.TransitionTile;
import net.cmr.gaze.world.interfaceTiles.ExploitableTile;
import net.cmr.gaze.world.interfaceTiles.SpeedChangeTile;

public class LavaTile extends TransitionTile implements SpeedChangeTile, LightSource, ExploitableTile {

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
	public void render(Gaze game, GameScreen screen, int x, int y) {
		draw(game.batch, game.getAnimation("lava").getKeyFrame(Tile.tileRenderDelta, true), x, y, 1, 1);
		//game.batch.draw(game.getAnimation("lava").getKeyFrame(Tile.tileRenderDelta, true), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
		super.render(game, screen, x, y);
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
		return 6;
	}
	
	@Override
	public Color getColor() {
		Color color = new Color(Gaze.getAverageColor(Gaze.get().getSprite("lava")));
		color = color.mul(1.5f);
		color.a = .25f;
		return color;
	}

	@Override
	public Item getExploitedItem() {
		return Items.getItem(ItemType.STEAM_CANISTER, 1);
	}

	@Override
	public ExploitType getExploitType() {
		return ExploitType.PUMP;
	} 
}

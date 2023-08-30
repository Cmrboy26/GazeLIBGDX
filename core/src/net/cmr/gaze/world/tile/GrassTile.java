package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Tool;
import net.cmr.gaze.inventory.Tool.ToolType;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.TransitionTile;
import net.cmr.gaze.world.World;

public class GrassTile extends TransitionTile {

	public GrassTile() {
		super(TileType.GRASS);
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
		//draw(game.batch, game.getSprite("grass"), x, y, 1, 1);
		draw(game.batch, game.getSprite("grass"+Math.abs(x%2)+Math.abs(y%2)), x, y, 1, 1);
		super.render(game, chunks, x, y);
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
	protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		if(clickType == 2) {
			if(world.getTile(x, y, 1) != null) {
				return false;
			}
			if(System.currentTimeMillis()-player.getPlayer().lastBreakInteraction>(1000f/player.getPlayer().getBreakSpeed())) {
				Item held = player.getPlayer().getHeldItem();
				if(held != null && held instanceof Tool && ((Tool)held).toolType()==ToolType.SHOVEL) {
					player.getPlayer().lastBreakInteraction = System.currentTimeMillis();
					world.addTile(Tiles.getTile(TileType.DIRT), x, y);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		return new GrassTile();
	}
	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}

}

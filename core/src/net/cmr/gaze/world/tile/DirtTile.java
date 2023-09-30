package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Tool;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Tool.ToolType;
import net.cmr.gaze.inventory.custom.WheatSeeds;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.TransitionTile;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Entity;

public class DirtTile extends TransitionTile {

	public DirtTile() {
		super(TileType.DIRT);
	}
	
	boolean persistent = false;
	
	public void setPersistence(boolean persistent) {
		this.persistent = persistent;
	}
	
	@Override
	public TileType[] belowWhitelist() {
		return null;
	}

	@Override
	public TileType[] belowBlacklist() {
		return null;
	}

	final String[] transitionSprite = new String[] {"sandTransition"};
	@Override
	public String[] getTransitionSprite() {
		return transitionSprite;
	}

	final TileType[] transitionTiles = new TileType[] {TileType.SAND};
	@Override
	public TileType[] getTransitionTiles() {
		return transitionTiles;
	}
	
	double lastCheck = 0;
	
	@Override
	public void update(TileData data, Point worldCoordinates) {
		if(persistent) {
			return;
		}
		if(data.isServer()) {
			lastCheck+=Tile.DELTA_TIME;
			if(lastCheck>5) {
				Tile[] adjacents = data.getAdjacents(worldCoordinates.x, worldCoordinates.y, 0);
				int grassTiles = 0;
				for(Tile adj : adjacents) {
					if(adj instanceof GrassTile) {
						grassTiles++;
					}
				}
				float chance = MathUtils.log(grassTiles+1, 8)/(8f*3f);
				
				if(grassTiles!=0&&Math.random()<chance) {
					data.addTile(Tiles.getTile(TileType.GRASS), worldCoordinates.x, worldCoordinates.y);
				}
				
				lastCheck = 0;
			}
		}
	}
	
	@Override
	protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		if(clickType == 2) {
			if(persistent) {
				setPersistence(false);
			}
			if(System.currentTimeMillis()-player.getPlayer().lastBreakInteraction>(1000f/player.getPlayer().getBreakSpeed())) {
				if(world.getTile(x, y, 1) == null) {
					Item held = player.getPlayer().getHeldItem();
					if(held != null && held instanceof Tool && ((Tool)held).toolType()==ToolType.HOE) {
						player.getPlayer().lastBreakInteraction = System.currentTimeMillis();
						world.addTile(Tiles.getTile(TileType.FARMLAND), x, y);
						return true;
					}
				}
			}
			/*if(player.getPlayer().getHeldItem() instanceof GrassSeeds) {
				world.addTile(Tiles.getTile(TileType.Grass), x, y);
				player.getPlayer().getInventory().remove(Items.getItem(ItemType.GrassSeeds, 1));
				player.inventoryChanged(true);
				return true;
			}*/
		}
		return false;
	}
	
	@Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		draw(game.batch, game.getSprite("dirt"), x, y, 1, 1);
		super.render(game, screen, x, y);
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		DirtTile tile = new DirtTile();
		tile.setPersistence(input.readBoolean());
		return tile;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		buffer.writeBoolean(persistent);
	}

}

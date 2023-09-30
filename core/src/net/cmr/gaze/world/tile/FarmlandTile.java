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
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.CropTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.TransitionTile;
import net.cmr.gaze.world.World;

public class FarmlandTile extends TransitionTile {

	public FarmlandTile() {
		super(TileType.FARMLAND);
		moisture = 0;
	}

	float moisture = 0;
	float lastCheck = 0;
	Tile onTop;
	
	@Override
	public TileType[] belowWhitelist() {
		return null;
	}

	@Override
	public TileType[] belowBlacklist() {
		return null;
	}

	final String[] transitionSprite = new String[] {"sandTransition", "dirtTransition", "grassTransition"};
	@Override
	public String[] getTransitionSprite() {
		return transitionSprite;
	}

	final TileType[] transitionTiles = new TileType[] {TileType.SAND, TileType.DIRT, TileType.GRASS};
	@Override
	public TileType[] getTransitionTiles() {
		return transitionTiles;
	}
	
	@Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		draw(game.batch, game.getSprite("farmland"+((moisture>0)?"Moist":"Dry")), x, y, 1, 1);
		super.render(game, screen, x, y);
	}
	
	boolean dry = false;
	
	public static final float CHECK_DURATION = 1;
	
	@Override
	public void update(TileData data, Point worldCoordinates) {
		
		int seconds = 180;
		if(moisture>0) {
			moisture-=1d/(seconds*20)*Math.random();
		}
		lastCheck += Tile.DELTA_TIME;
		
		if(lastCheck > CHECK_DURATION) {
			onTop = data.getTile(worldCoordinates.x, worldCoordinates.y, 1);
			//System.out.println(moisture+":"+onTop);
			if(moisture <= 0) {
				lastCheck=0;
				dry = true;
				if(data.isServer()) {
					World world = data.getServerData();
					world.onTileChange(worldCoordinates.x, worldCoordinates.y, 0);
				}
			} else if(onTop instanceof CropTile) {
				((CropTile)onTop).updateCrop(moisture, data, worldCoordinates.x, worldCoordinates.y);
			}
			lastCheck = 0;
		}
		
		if(dry && moisture > 0) {
			dry = false;
			if(data.isServer()) {
				World world = data.getServerData();
				world.onTileChange(worldCoordinates.x, worldCoordinates.y, 0);
			}
		}
	}
	
	@Override
	protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		if(clickType == 2) {
			if(System.currentTimeMillis()-player.getPlayer().lastBreakInteraction>(1000f/player.getPlayer().getBreakSpeed())) {
				Item held = player.getPlayer().getHeldItem();
				if(held != null && held instanceof Tool && ((Tool)held).toolType()==ToolType.SHOVEL) {
					player.getPlayer().lastBreakInteraction = System.currentTimeMillis();
					world.addTile(Tiles.getTile(TileType.DIRT), x, y);
					return true;
				}
			}
			/*if(player.getPlayer().getHeldItem() instanceof SeedItem) {
				SeedItem seeds = (SeedItem) player.getPlayer().getHeldItem();
			}*/
			
			/*if(player.getPlayer().getHeldItem() instanceof GrassSeeds) {
				world.addTile(Tiles.getTile(TileType.Grass), x, y);
				player.getPlayer().getInventory().remove(Items.getItem(ItemType.GrassSeeds, 1));
				player.inventoryChanged(true);
				return true;
			}*/
			if(player.getPlayer().getHeldItem() instanceof Tool && ((Tool)player.getPlayer().getHeldItem()).toolType()==ToolType.WATERING_CAN) {
				setMoisture(1, world, x, y);
				return true;
			}
		}
		return false;
	}
	
	public void setMoisture(float moisture, World world, int x, int y) {
		boolean wasDry = this.moisture<=0;
		boolean isDry = moisture<=0;
		this.moisture = moisture;
		if(wasDry^isDry) {
			lastCheck = 0;
			world.onTileChange(x, y, 0);
		}
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		FarmlandTile farm = new FarmlandTile();
		farm.moisture = input.readFloat();
		return farm;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		buffer.writeFloat(this.moisture);
	}

}

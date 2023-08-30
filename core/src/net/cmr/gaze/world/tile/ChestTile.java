package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Inventory;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.world.BreakableUtils;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class ChestTile extends Tile {

	Inventory inventory;
	
	public ChestTile() {
		super(TileType.CHEST);
		//inventory.add(Items.getItem(ItemType.IronOre, 5));
		//inventory.add(Items.getItem(ItemType.Stone, 50));
	}

	@Override
	public TileType[] belowWhitelist() {
		return null;
	}

	@Override
	public void update(TileData data, Point worldCoordinates) {
		if(data.isServer()) {
			data.getServerData().onTileChange(worldCoordinates.x, worldCoordinates.y, 1);
		}
	}
	
	@Override
	public TileType[] belowBlacklist() {
		return getDefaultBlacklist();
	}
	
	public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE/2);
	}
	
	@Override
	public String getHitNoise() {
		return "woodHit";
	}
	@Override
	public String getBreakNoise() {
		return "woodHit";
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		for(Item item : inventory.getAll()) {
			if(item == null) {
				continue;
			}
			BreakableUtils.dropItem(world, x, y, item);
		}
		BreakableUtils.dropItem(world, x, y, Items.getItem(ItemType.CHEST, 1));
	}
	
	
	@Override
	protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		if(clickType == 2) {
			player.setOpenContainer(this, x, y);
			return true;
		}
		return false;
	}
	
	@Override
	public void render(Gaze game, HashMap<Point, Tile[][][]> chunks, int x, int y) {
		draw(game.batch, game.getSprite("chest"), x, y, 1, 1);
		super.render(game, chunks, x, y);
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		ChestTile chest = new ChestTile();
		Tile.readBreakData(input, chest);
		chest.inventory = Inventory.readInventory(input);
		return chest;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		getInventory().writeInventory(buffer);
	}
	
	public Inventory getInventory() {
		if(inventory == null) {
			inventory = new Inventory(3*7);
		}
		return inventory;
	}

}

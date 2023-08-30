package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.crafting.Crafting.CraftingStation;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.world.BreakableUtils;
import net.cmr.gaze.world.CraftingStationTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class TableTile extends Tile implements CraftingStationTile {

	public TableTile() {
		super(TileType.TABLE);
	}

	@Override
	public TileType[] belowWhitelist() {
		return null;
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
	public CraftingStation getStation() {
		return CraftingStation.TABLE;
	}
	
	@Override
	protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		if(clickType == 2) {
			//player.server.getManager().getWorld(player.getPlayer().getWorld().getWorldName().equals("default")?"default2":"default").addPlayer(player);
			player.setCraftingStation(this, x, y);
			return true;
		}
		return false;
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		BreakableUtils.dropItem(world, x, y, Items.getItem(ItemType.TABLE, 1));
	}
	
	@Override
	public void render(Gaze game, HashMap<Point, Tile[][][]> chunks, int x, int y) {
		draw(game.batch, game.getSprite("table"), x, y, 1, 1);
		//game.batch.draw(game.getSprite("table"), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
		super.render(game, chunks, x, y);
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		TableTile table = new TableTile();
		Tile.readBreakData(input, table);
		return table;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}

}

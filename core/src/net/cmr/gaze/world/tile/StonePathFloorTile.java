package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Tool.Material;
import net.cmr.gaze.inventory.Tool.ToolType;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.FloorTile;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.interfaceTiles.SpeedChangeTile;

public class StonePathFloorTile extends FloorTile {

	public StonePathFloorTile() {
		super(TileType.STONE_PATH_FLOOR);
	}

	@Override
	public TileType[] belowWhitelist() {
		return null;
	}

	@Override
	public TileType[] belowBlacklist() {
		return Tile.defaultBlacklist;
	}

	@Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		super.renderBelowTile(game, screen, x, y);
		draw(game.batch, game.getSprite("stonePathFloor"), x, y, 1, 1);
		//game.batch.draw(game.getSprite("stonePathFloor"), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
		super.render(game, screen, x, y);
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		StonePathFloorTile wft = new StonePathFloorTile();
		Tile.readBreakData(input, wft);
		wft.readFloorTileData(input);
		return wft;
	}

	@Override
	public Material getMaterial() {
		return Material.STONE;
	}
	public ToolType getToolType() {
		return ToolType.PICKAXE;
	}
	
	@Override
	public String getHitNoise() {
		return "stoneHit";
	}
	@Override
	public String getBreakNoise() {
		return "stoneHit";
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		super.onBreak(world, player, x, y);
		TileUtils.dropItem(world, x, y, Items.getItem(ItemType.STONE_PATH_FLOOR, 1));
	}

	@Override
	public float getSpeedMultiplier() {
		return 1.15f;
	}
	
}

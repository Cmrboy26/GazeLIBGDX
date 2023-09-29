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
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.FloorTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class StoneBrickFloorTile extends FloorTile {

	public StoneBrickFloorTile() {
		super(TileType.STONE_BRICK_FLOOR);
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
		draw(game.batch, game.getSprite("stoneBrickFloor"), x, y, 1, 1);
		super.render(game, chunks, x, y);
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		StoneBrickFloorTile wft = new StoneBrickFloorTile();
		Tile.readBreakData(input, wft);
		wft.readFloorTileData(input);
		return wft;
	}

	@Override
	public Material getMaterial() {
		return Material.WOOD;
	}
	public ToolType getToolType() {
		return ToolType.AXE;
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
		TileUtils.dropItem(world, x, y, Items.getItem(ItemType.STONE_BRICK_FLOOR, 1));
	}

	@Override
	public float getSpeedMultiplier() {
		return 1.15f;
	}
	
}

package net.cmr.gaze.world.tile;

import java.io.DataInputStream;
import java.io.IOException;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Tool.Material;
import net.cmr.gaze.inventory.Tool.ToolType;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.FloorTile;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.interfaceTiles.HousingFloor;

public class WoodFloorTile extends FloorTile implements HousingFloor {

	public WoodFloorTile() {
		super(TileType.WOOD_FLOOR);
	}

	final String[] transitionSprite = new String[] {"woodTransition"};
	@Override
	public String[] getTransitionSprite() {
		return transitionSprite;
	}
	final TileType[] transitionTiles = new TileType[] {};
	@Override
	public TileType[] getTransitionTiles() {
		return transitionTiles;
	}

	@Override
	public boolean transitionAllExcludeSelf() {
		return true;
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
		draw(game.batch, game.getSprite("woodFloor"), x, y, 1, 1);
		super.render(game, screen, x, y);
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		WoodFloorTile wft = new WoodFloorTile();
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
		return "woodHit";
	}
	@Override
	public String getBreakNoise() {
		return "woodHit";
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		super.onBreak(world, player, x, y);
		TileUtils.dropItem(world, x, y, Items.getItem(ItemType.WOOD_FLOOR, 1));
	}

	@Override
	public float getSpeedMultiplier() {
		return 1.15f;
	}
	
}

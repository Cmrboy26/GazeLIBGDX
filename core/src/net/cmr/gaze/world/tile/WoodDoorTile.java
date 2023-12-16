package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

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
import net.cmr.gaze.world.abstractTiles.RotatableTile;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.interfaceTiles.HousingDoor;
import net.cmr.gaze.world.interfaceTiles.Rotatable;

public class WoodDoorTile extends RotatableTile implements HousingDoor {

	public WoodDoorTile() {
		super(TileType.WOOD_DOOR);
	}

	@Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		draw(game.batch, game.getSprite("woodDoor"+(getDirection()+1)), x, y-1, 1, 3);
		//game.batch.draw(game.getSprite("woodDoor"+(getDirection()+1)), x*TILE_SIZE, y*TILE_SIZE-Tile.TILE_SIZE, TILE_SIZE, TILE_SIZE*3);
		super.render(game, screen, x, y);
	}

	@Override
	public String getWallSpriteName() {
		return "woodDoor"+(getDirection()+1);
	}
	
	@Override
	public TileType[] belowWhitelist() {
		return null;
	}

	@Override
	public TileType[] belowBlacklist() {
		return getDefaultBlacklist();
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		WoodDoorTile wall = new WoodDoorTile();
		Tile.readBreakData(input, wall);
		Rotatable.readRotatableData(input, wall);
		return wall;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		writeRotatableData(buffer);
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
		TileUtils.dropItem(world, x, y, Items.getItem(ItemType.WOOD_DOOR, 1));
	}
	
	/*public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
	}*/

}

package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Tool.Material;
import net.cmr.gaze.inventory.Tool.ToolType;
import net.cmr.gaze.leveling.Skills.Skill;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.HousingWall;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.WallTile;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class StoneBrickWallTile extends Tile implements HousingWall {

	public StoneBrickWallTile() {
		super(TileType.STONE_BRICK_WALL);
	}

	@Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		//draw(game.batch, game.getSprite("stoneBrickWall"), x, y-1, 1, 3);
		//game.batch.draw(game.getSprite("woodWall"), x*TILE_SIZE, y*TILE_SIZE-Tile.TILE_SIZE, TILE_SIZE, TILE_SIZE*3);
		HousingWall.render(game, screen, this, x, y);
		super.render(game, screen, x, y);
	}

	@Override
	public String getWallSpriteName() {
		return "stoneBrickWall";
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
		StoneBrickWallTile wall = new StoneBrickWallTile();
		Tile.readBreakData(input, wall);
		return wall;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
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
		return "stoneBreak";
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		TileUtils.dropItem(world, x, y, Items.getItem(ItemType.STONE_BRICK_WALL, 1));
	}
	
	public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
	}

}

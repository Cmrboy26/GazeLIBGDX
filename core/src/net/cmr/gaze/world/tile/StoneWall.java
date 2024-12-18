package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

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
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.interfaceTiles.WallTile;

public class StoneWall extends Tile implements WallTile {

	public StoneWall() {
		super(TileType.STONE_WALL);
	}

	@Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		draw(game.batch, game.getSprite("stoneWall"+TileUtils.getBreakSpriteInt(this, 3)), x, y-1, 1, 3);
		//game.batch.draw(game.getSprite("stoneWall"+BreakableUtils.getBreakSpriteInt(this, 3)), x*TILE_SIZE, y*TILE_SIZE-Tile.TILE_SIZE, TILE_SIZE, TILE_SIZE*3);
		super.render(game, screen, x, y);
	}
	
	@Override
	protected void onHit(World world, Player player, int x, int y) {
		super.onHit(world, player, x, y);
		TileUtils.spawnBreakParticle(world, this, x, y, this);
		TileUtils.spawnBreakParticle(world, this, x, y+.8f, this);
	}
	
	@Override
	public TileType[] belowWhitelist() {
		return new TileType[] {TileType.STONE, TileType.DIRT};
	}

	@Override
	public TileType[] belowBlacklist() {
		return null;
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		StoneWall wall = new StoneWall();
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
	public int getBreakLevel() {
		return 1;
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
		TileUtils.spawnBreakParticle(world, this, x, y+.8f, this);
		TileUtils.spawnBreakParticle(world, this, x, y+.4f, this);
		TileUtils.spawnBreakParticle(world, this, x, y, this);
		TileUtils.addPlayerXP(player, world, Skill.MINING, 2f);
		TileUtils.dropItem(world, x, y, Items.getItem(ItemType.STONE, 1));
		if(0==new Random().nextInt(2)) {
			TileUtils.dropItem(world, x, y, Items.getItem(ItemType.STONE, 1));
		}
	}
	
	public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
	}

}

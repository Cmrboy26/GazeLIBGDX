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

public class CopperOreWall extends Tile implements WallTile {

	public CopperOreWall() {
		super(TileType.COPPER_ORE_WALL);
	}

	@Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		draw(game.batch, game.getSprite("copperOreWall"+TileUtils.getBreakSpriteInt(this, 3)), x, y-1, 1, 3);
		//draw(game.batch, game.getSprite("campfire"), x, y, 1, 1);
		//game.batch.draw(, x*TILE_SIZE, y*TILE_SIZE-Tile.TILE_SIZE, TILE_SIZE, TILE_SIZE*3);
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
		CopperOreWall wall = new CopperOreWall();
		Tile.readBreakData(input, wall);
		return wall;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}
	
	@Override
	public Material getMaterial() {
		return Material.COPPER;
	}
	public ToolType getToolType() {
		return ToolType.PICKAXE;
	}
	@Override
	public int getBreakLevel() {
		return 2;
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
	public String getAmbientNoise(GameScreen game) {
		return "caveAmbience0"/*+new Random(this.hashCode()).nextInt(1)*/;
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		TileUtils.spawnBreakParticle(world, this, x, y+.8f, this);
		TileUtils.spawnBreakParticle(world, this, x, y+.4f, this);
		TileUtils.spawnBreakParticle(world, this, x, y, this);
		TileUtils.addPlayerXP(player, world, Skill.MINING, 8);
		TileUtils.dropItem(world, x, y, Items.getItem(ItemType.COPPER_ORE, 1));
		if(0==new Random().nextInt(3)) {
			TileUtils.dropItem(world, x, y, Items.getItem(ItemType.COPPER_ORE, 1));
		}
	}
	
	public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
	}

}

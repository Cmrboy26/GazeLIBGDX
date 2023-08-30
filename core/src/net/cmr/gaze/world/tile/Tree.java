package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Tool.Material;
import net.cmr.gaze.inventory.Tool.ToolType;
import net.cmr.gaze.leveling.Skills.Skill;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.util.CustomMath;
import net.cmr.gaze.world.BaseTile;
import net.cmr.gaze.world.BreakableUtils;
import net.cmr.gaze.world.SeeThroughTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class Tree extends BaseTile implements SeeThroughTile {
	
	float random = 0;
	
	public Tree() {
		super(TileType.TREE, 2, 1);
	}

	@Override
	public TileType[] belowWhitelist() {
		return new TileType[] {TileType.GRASS};
	}

	@Override
	public TileType[] belowBlacklist() {
		return null;
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
	
	float shake;
	final int shakePeriod = 10;
	
	@Override
	public void update(TileData data, Point worldCoordinates) {
		if(shake > 0) {
			shake-=Tile.DELTA_TIME;
		}
		if(shake < 0) {
			shake = 0;
		}
	}
	
	@Override
	protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		if(clickType == 2) {
			world.playSound("grassBreak", 1f, x, y);
			shake+=.5f;
			shake = CustomMath.minMax(0, shake, .75f);
			world.onTileChange(x, y, getType().layer);
			return true;
		}
		return false;
	}
	
	@Override
	public void render(Gaze game, HashMap<Point, Tile[][][]> chunks, int x, int y) {
		if(random == 0) {
			random = (new Random(x+y*37*37).nextFloat()*1f/4f)-1f/8f;
		}
		
		if(shake > 0) {
			shake -= Gdx.graphics.getDeltaTime();
		}
		
		float shakeX = 2.1f*shake*MathUtils.sin(shakePeriod*MathUtils.PI2*shake);
		float endShakeX = shakeX*1/16f;

		;
		
		draw(game.batch, game.getSprite("tree"+(getRandomizedInt(1, x, y)+2)), x+random+endShakeX, y, 2, 3);
		//game.batch.draw(game.getSprite("tree1"), x*TILE_SIZE+random+endShakeX, y*TILE_SIZE, TILE_SIZE*2, TILE_SIZE*3);
		
		super.render(game, chunks, x, y);
	}
	
	@Override
	protected void onHit(World world, Player player, int x, int y) {
		BreakableUtils.spawnParticle(world, this, x+.5f, y, .9f, this);
		shake+=.5f;
		shake = CustomMath.minMax(0, shake, .75f);
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		BreakableUtils.spawnParticle(world, this, x+.5f, y, .9f, this);
		BreakableUtils.addPlayerXP(player, world, Skill.FORAGING, 3);
		BreakableUtils.dropItem(world, x, y, Items.getItem(ItemType.WOOD, 3));
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		Tree tree = new Tree();
		Tile.readBreakData(input, tree);
		tree.shake = input.readFloat();
		return tree;
	}
	
	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		buffer.writeFloat(shake);
	}
	
	
	
	@Override
	public String getAmbientNoise(GameScreen game) {
		return "forestAmbience"+new Random().nextInt(5);
	}
	@Override
	public float getAmbientNoiseVolume() {
		return .25f;
	}
	
	@Override
	public float getAmbientNoisePitch() {
		return 1f+new Random().nextFloat()/5f-1/10f;
	}
	
	public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE+Tile.TILE_SIZE/2, y*TILE_SIZE, TILE_SIZE, TILE_SIZE/2);
	}

}

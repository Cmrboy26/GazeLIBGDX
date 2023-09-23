package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.world.BreakableUtils;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class TallGrassTile extends Tile {

	public TallGrassTile() {
		super(TileType.TALL_GRASS);
	}

	@Override
	public TileType[] belowWhitelist() {
		return new TileType[] {TileType.GRASS, TileType.DIRT};
	}

	@Override
	public TileType[] belowBlacklist() {
		return null;
	}

	@Override
	public void render(Gaze game, HashMap<Point, Tile[][][]> chunks, int x, int y) {
		draw(game.batch, game.getSprite("tallGrass"+(getRandomizedInt(1, x, y)+1)), x-.5f, y-.5f, 2, 2);
		//game.batch.draw(game.getSprite("tallGrass"), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
		super.render(game, chunks, x, y);
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		TallGrassTile tg = new TallGrassTile();
		Tile.readBreakData(input, tg);
		return tg;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}
	
	@Override
	public String getBreakNoise() {
		return "grassBreak";
	}
	
	@Override
	public boolean isInstantBreak() {
		return true;
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		//player.addXP(world, Skill.Foraging, .1);
		BreakableUtils.spawnBreakParticleOffset(world, this, x, y, 0, this);
		Random random = new Random();
		if(random.nextInt(4)==0) {
			BreakableUtils.dropItem(world, x, y, Items.getItem(ItemType.WHEAT_SEEDS, 1));
		}
	}

}

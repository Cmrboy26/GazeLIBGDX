package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.leveling.Skills.Skill;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.CropTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class CottonTile extends CropTile {
	
	public CottonTile() {
		super(TileType.COTTON);
	}

	@Override
	public int getGrowthStages() {
		return 5;
	}

	@Override
	public int getAverageTimePerStage() {
		return 45;
	}
	
	@Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		//draw(game.batch, game.getSprite("wheat"+stage), x, y, 1, 1);
		//String wheat = getRandomizedInt(2, x, y)==0?"wheat":"newWheat";
		//if(wheat.equals("wheat")) {
		//	draw(game.batch, game.getSprite("wheat"+stage), x, y, 1, 1);
		//} else {
		draw(game.batch, game.getSprite("cotton"+stage), x-.5f, y-.5f, 2f, 2f);
		//}
		super.render(game, screen, x, y);
	}

	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		CottonTile tile = new CottonTile();
		Tile.readBreakData(input, tile);
		tile.stage = input.readInt();
		return tile;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		buffer.writeInt(stage);
	}
	
	@Override
	public String getBreakNoise() {
		return "grassBreak";
	}
	
	@Override
	public float getRenderYOffset() {
		if(stage <= 3) {
			return 1f;
		}
		return .25f;
		/*if(stage <= 3) {
			return (int) -Tile.TILE_SIZE;
		}
		return (int) (-Tile.TILE_SIZE/4f);*/
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		if(stage == getGrowthStages()) {
			TileUtils.spawnBreakParticleOffset(world, this, x, y, .5f, this);
			TileUtils.addPlayerXP(player, world, Skill.FARMING, 1.5f);
			TileUtils.dropItem(world, x, y, Items.getItem(ItemType.COTTON_SEEDS, (int) (1+Math.round(2*Math.random()))));
			TileUtils.dropItem(world, x, y, Items.getItem(ItemType.COTTON, 1));
		}
	}

}

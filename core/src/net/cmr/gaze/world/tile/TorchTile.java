
package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.custom.TorchItem;
import net.cmr.gaze.world.BreakableUtils;
import net.cmr.gaze.world.LightSource;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class TorchTile extends Tile implements LightSource {

	public TorchTile() {
		super(TileType.TORCH);
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
	public boolean isInstantBreak() {
		return true;
	}
	
	@Override
	public void render(Gaze game, HashMap<Point, Tile[][][]> chunks, int x, int y) {
		draw(game.batch, game.getAnimation("torch").getKeyFrame(Tile.tileRenderDelta, true), x, y, 1, 1);
		//game.batch.draw(game.getAnimation("torch").getKeyFrame(Tile.tileRenderDelta, true), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
		super.render(game, chunks, x, y);
	}
	@Override
	public int getRenderYOffset() {
		return (int) Tile.TILE_SIZE/-10;
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		TorchTile torch = new TorchTile();
		Tile.readBreakData(input, torch);
		return torch;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}

	@Override
	public float getIntensity() {
		return 6f+TorchItem.getTorchPulse(this);
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		BreakableUtils.dropItem(world, x, y, Items.getItem(ItemType.TORCH, 1));
	}
	
	@Override
	public String getHitNoise() {
		return "woodHit";
	}
	@Override
	public String getBreakNoise() {
		return "woodHit";
	}
	
	public static final Color TORCH_COLOR = Color.WHITE.cpy().lerp(Color.ORANGE, .4f);

	@Override
	public Color getColor() {
		return TORCH_COLOR;
	}

}

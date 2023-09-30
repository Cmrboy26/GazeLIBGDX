package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.crafting.Crafting.CraftingStation;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Tool.Material;
import net.cmr.gaze.inventory.custom.TorchItem;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.CraftingStationTile;
import net.cmr.gaze.world.LightSource;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class FurnaceTile extends Tile implements CraftingStationTile, LightSource {

	public FurnaceTile() {
		super(TileType.FURNACE);
	}

	@Override
	public TileType[] belowWhitelist() {
		return null;
	}

	@Override
	public TileType[] belowBlacklist() {
		return getDefaultBlacklist();
	}

	public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE/2);
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
	public Material getMaterial() {
		return Material.STONE;
	}
	@Override
	public int getBreakLevel() {
		return 1;
	}
	
	@Override
	public CraftingStation getStation() {
		return CraftingStation.FURNACE;
	}
	
	@Override
	protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		if(clickType == 2) {
			player.setCraftingStation(this, x, y);
			return true;
		}
		return false;
	}
	
	@Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		//draw(game.batch, game.getSprite("furnace"), x, y, 1, 1.5f);
		draw(game.batch, game.getAnimation("furnace").getKeyFrame(Tile.tileRenderDelta, true), x-.5f, y-.5f, 2, 2);
		super.render(game, screen, x, y);
	}
	
	@Override
	public void onBreak(World world, Player player, int x, int y) {
		TileUtils.dropItem(world, x, y, Items.getItem(ItemType.FURNACE, 1));
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		FurnaceTile table = new FurnaceTile();
		Tile.readBreakData(input, table);
		return table;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}

	@Override
	public float getIntensity() {
		return 6+TorchItem.getTorchPulse(this);
	}
	@Override
	public Color getColor() {
		return TorchTile.TORCH_COLOR;
	}

}

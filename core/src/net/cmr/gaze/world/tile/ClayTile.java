package net.cmr.gaze.world.tile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Tool;
import net.cmr.gaze.inventory.Tool.ToolType;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.DrillableTile;
import net.cmr.gaze.world.abstractTiles.TransitionTile;
import net.cmr.gaze.world.entities.Particle.ParticleEffectType;

public class ClayTile extends TransitionTile implements DrillableTile {

	public ClayTile() {
		super(TileType.CLAY);
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
		draw(game.batch, game.getSprite("clay"), x, y, 1, 1);
		super.render(game, screen, x, y);
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		return new ClayTile();
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}

	@Override
	protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		if(clickType == 2) {
			if(System.currentTimeMillis()-player.getPlayer().lastBreakInteraction>(1000f/player.getPlayer().getBreakSpeed())) {
				if(world.getTile(x, y, 1) == null) {
					Item held = player.getPlayer().getHeldItem();
					if(held != null && held instanceof Tool && ((Tool)held).toolType()==ToolType.SHOVEL) {
						player.getPlayer().lastBreakInteraction = System.currentTimeMillis();
						world.addTile(Tiles.getTile(TileType.SANDSTONE), x, y);
						TileUtils.dropItem(world, x, y, Items.getItem(ItemType.CLAY, new Random().nextInt(3)+1));
						world.playSound("dirt", .8f, x, y);
						TileUtils.spawnParticleOffset(world, ParticleEffectType.HOE, this, x, y+2, -2, Color.valueOf("#afb7c3"));
						return true;
					}
				}
			}
			/*if(player.getPlayer().getHeldItem() instanceof GrassSeeds) {
				world.addTile(Tiles.getTile(TileType.Grass), x, y);
				player.getPlayer().getInventory().remove(Items.getItem(ItemType.GrassSeeds, 1));
				player.inventoryChanged(true);
				return true;
			}*/
		}
		return false;
	}
	
	final String[] transitionSprite = new String[] {"grassTransition", "dirtTransition", "waterTransition", "sandTransition"};
	@Override
	public String[] getTransitionSprite() {
		return transitionSprite;
	}

	final TileType[] transitionTiles = new TileType[] {TileType.GRASS, TileType.DIRT, TileType.WATER, TileType.SAND};
	@Override
	public TileType[] getTransitionTiles() {
		return transitionTiles;
	}

	@Override
	public Item getExploitedItem() {
		return Items.getItem(ItemType.CLAY, 1);
	}

	@Override
	public float getDrillTime() {
		return DRILL_TIME_TIER_2;
	}

}

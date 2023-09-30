package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.networking.packets.AudioPacket;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class ChuteTile extends Tile {

	public ChuteTile() {
		super(TileType.CHUTE);
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
	public void render(Gaze game, GameScreen screen, int x, int y) {
		draw(game.batch, game.getSprite("chute"), x, y, 1, 1);
		super.render(game, screen, x, y);
	}
	
	@Override
	public float getRenderYOffset() {
		return .5f;
		//return (int) -Tile.TILE_SIZE/2;
	}
	
	@Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		ChuteTile chute = new ChuteTile();
		Tile.readBreakData(input, chute);
		return chute;
	}

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}
	
	@Override
	protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		if(clickType == 2) {
			
			World target = player.server.getWorldManager().getUndergroundWorld(world);
			
			Tile at = target.getTile(x, y, 1);
			Tile under = target.getTile(x, y, 0);
			if(under != null) {
				TileType replace = target.getGenerator().isUnderground()?TileType.STONE:TileType.SAND;
				if(under.getType()==TileType.WATER) {
					target.setTile(Tiles.getTile(replace), x, y);
				} else if(under.getType()==TileType.LAVA) {
					target.setTile(Tiles.getTile(replace), x, y);
				}
			}
			
			if(at!=null && at.getType()!=TileType.CHUTE) {
				target.removeTile(x, y, 1, true);
			}
			boolean placed = target.addTile(Tiles.getTile(TileType.CHUTE), x, y);
			
			at = target.getTile(x, y, 1);
			
			if(target.getGenerator().isUnderground()) {
				target.removeTile(x, y-1, 1, true);
			}
			
			if(at!=null&&at.getType()==TileType.CHUTE) {
				player.getSender().addPacket(new AudioPacket("chute", 1f));
				target.addPlayer(player);
				player.getPlayer().setPositionKeepLast(x*Tile.TILE_SIZE+Tile.TILE_SIZE/2, y*Tile.TILE_SIZE+Tile.TILE_SIZE/4);
			}
			
			return true;
		}
		return false;
	}

	@Override
	public void onPlace(World world, int x, int y, Player player) {
		World target = world.getServer().getWorldManager().getUndergroundWorld(world);
		target.removeTile(x, y, 1);
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
	public void onBreak(World world, Player player, int x, int y) {
		World target = world.getServer().getWorldManager().getUndergroundWorld(world);
		Tile at = target.getTile(x, y, 1);
		if(at!=null&&at.getType()==TileType.CHUTE) {
			target.removeTile(x, y, 1);
		}
		TileUtils.dropItem(world, x, y, Items.getItem(ItemType.CHUTE, 1));
	}
	
}

package net.cmr.gaze.world;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Tool.Material;
import net.cmr.gaze.inventory.Tool.ToolType;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.TileType.Replaceable;
import net.cmr.gaze.world.entities.Player;

public abstract class Tile implements Cloneable {

	public static final float TILE_SIZE = 10;
	public static final double DELTA_TIME = 1/20d;
	
	public static float tileRenderDelta = 0;
	
	private TileType tileType;
	private int breakAmount;
	
	public Tile(TileType tileType) {
		this.tileType = tileType;
		if(isBreakable()) {
			breakAmount = tileType.breakAmount;
		}
	}
	
	public abstract TileType[] belowWhitelist();
	public abstract TileType[] belowBlacklist();
	
	protected static final TileType[] defaultBlacklist = new TileType[] {TileType.WATER, TileType.LAVA};
	
	public final TileType[] getDefaultBlacklist() {
		return defaultBlacklist;
	}
	
	// must be added for any tile thats breakable in the readTile method
	public static void readBreakData(DataInputStream input, Tile tile) throws IOException {
		if(tile.isBreakable()) {
			tile.breakAmount = input.readInt();
		}
	}
	
	public abstract Tile readTile(DataInputStream input, TileType type) throws IOException;
	protected abstract void writeTile(TileType tile, DataBuffer buffer) throws IOException;
	
	public int getRenderYOffset() {
		return 0;
	}
	public void update(TileData data, Point worldCoordinates) {
		
	}
	
	public void draw(Batch batch, Sprite sprite, float x, float y, float width, float height) {
		
		Color average = Tiles.getAverageColor(this.getType());
		if(average == null) {
			average = Gaze.getAverageColor(sprite);
			Tiles.setAverageColor(tileType, average);
		}
		
		batch.draw(sprite, x*Tile.TILE_SIZE, y*Tile.TILE_SIZE, width*TILE_SIZE, height*Tile.TILE_SIZE);
	}
	public void draw(Batch batch, TextureRegion sprite, float x, float y, float width, float height) {
		
		Color average = Tiles.getAverageColor(this.getType());
		if(average == null) {
			average = Gaze.getAverageColor(sprite);
			Tiles.setAverageColor(tileType, average);
		}
		
		batch.draw(sprite, x*Tile.TILE_SIZE, y*Tile.TILE_SIZE, width*TILE_SIZE, height*Tile.TILE_SIZE);
	}
	
	public void render(Gaze game, HashMap<Point, Tile[][][]> chunks, int x, int y) {
		if(Gaze.HITBOXES) {
			if(getBoundingBox(x, y)!=null) {
				game.batch.draw(game.getSprite("hitbox"), getBoundingBox(x, y).getX(), getBoundingBox(x, y).getY(), getBoundingBox(x, y).width, getBoundingBox(x, y).height);
			}
		}
	}
	
	public TileType getType() {
		return tileType;
	}
	
	public static void writeOutgoingTile(Tile tile, DataBuffer buffer) throws IOException {
		if(tile == null) {
			buffer.writeInt(-1);
			return;
		} else if(tile instanceof StructureTile) {
			buffer.writeInt(-2);
			tile.writeTile(tile.tileType, buffer);
		} else {
			buffer.writeInt(tile.getType().getID());
			if(tile.isBreakable()) {
				buffer.writeInt(tile.breakAmount);
			}
			tile.writeTile(tile.tileType, buffer);
		}
	};
	public static Tile readIncomingTile(DataInputStream input) throws IOException {
		int index = input.readInt();
		//System.out.println(index);
		if(index == -1) {
			return null;
		}
		if(index == -2) {
			// structureTile
			return new StructureTile(TileType.values()[input.readInt()], input.readInt(), input.readInt());
		}
		
		TileType type = TileType.getItemTypeFromID(index);
		
		return Tiles.getTile(type, input);
	};
	
	public Tile clone() {
		try {
			return (Tile) super.clone();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getType().ordinal()+"";
	}
	
	public boolean isBreakable() {
		return isInstantBreak() || tileType.breakAmount != 0;
	}
	public int getBreakAmount() {
		return breakAmount;
	}
	public Material getMaterial() {
		return null;
	}
	public ToolType getToolType() {
		return null;
	}
	public int getBreakLevel() {
		return 0;
	}
	public boolean isInstantBreak() {
		return false;
	}
	
	/**
	 * 
	 * @param x coordinate of the tile
	 * @param y coordinate of the tile
	 * @return bounding box of the tile, returns null if theres no bounds/collision
	 */
	public Rectangle getBoundingBox(int x, int y) {
		return null;
	}
	
	/**
	 * @return whether or not the interact event was successful
	 */
	public final boolean onInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		// if the tile is breakable, try breaking it
		// do the overwritten interact event stuff
		// return the result
		
		if(this instanceof StructureTile) {
			StructureTile struct = ((StructureTile) this);
			return struct.getBaseTile(world, x, y).onInteract(player, world, x-struct.x, y-struct.y, clickType);
		}
		
		boolean result = false;
		if(isBreakable()) {
			result = breakInteract(player, world, x, y, clickType);
		}
		if(result) {
			return result;
		}
		result = overrideOnInteract(player, world, x, y, clickType);
		
		return result;
	}
	
	public boolean onInteractClient(GameScreen screen, int clickType) {
		return false;
	}
	
	// Method is only run when the tile is breakable
	protected final boolean breakInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		if(clickType == Input.Buttons.LEFT) {

			if(!isInstantBreak() && System.currentTimeMillis()-player.getPlayer().lastBreakInteraction<(1000f/player.getPlayer().getBreakSpeed())) {
				return false;
			}
			
			breakAmount-=player.getPlayer().getBreakAmount(this);
			if(getBreakAmount() <= 0) {
				world.removeTile(x, y, getType().layer);
				onBreak(world, player.getPlayer(), x, y);
				if(getBreakNoise()!=null) world.playSound(getBreakNoise(), 1f, x, y);
			} else {
				onHit(world, player.getPlayer(), x, y);
				if(getHitNoise()!=null) world.playSound(getHitNoise(), 1f, x, y);
			}
			if(player.getPlayer().getBreakAmount(this)!=0) {
				world.onTileChange(x, y, getType().layer);
			}
			
			if(isInstantBreak()) {
				player.getPlayer().lastBreakInteraction = 0;
			} else {
				player.getPlayer().lastBreakInteraction = System.currentTimeMillis();
			}
			
			return true;
		}
		return false;
	}
	
	protected void onHit(World world, Player player, int x, int y) {
		
	}
	
	protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		return false;
	}
	
	public void onBreak(World world, Player player, int x, int y) {
		
	}
	
	public String getHitNoise() {
		return null;
	}
	public String getBreakNoise() {
		return null;
	}
	public String getAmbientNoise(GameScreen game) {
		return null;
	}
	public float getAmbientNoiseVolume() {
		return 1f;
	}
	
	
	public boolean onInteractHit(PlayerConnection player, World world, int x, int y, int clickType) {
		return false;
	}
	
	int randomSprite = -1;
	
	public int getRandomizedInt(int maxInclusive, int x, int y) {
		if(randomSprite == -1) {
			randomSprite = new Random(x/2+y*37*37*37).nextInt(maxInclusive+1);
		}
		return randomSprite;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Tile) {
			Tile tile = (Tile) obj;
			if(getType()==tile.getType()) {
				return true;
			}
		}
		if(obj instanceof TileType) {
			TileType type = (TileType) obj;
			if(getType()==type) {
				return true;
			}
		}
		return false;
	}

	public float getAmbientNoisePitch() {
		return 1f;
	}
	
	public final Replaceable getReplaceability() {
		return getType().replaceable;
	}

	public void onPlace(World world, int x, int y, Player player) {
		
	}
}

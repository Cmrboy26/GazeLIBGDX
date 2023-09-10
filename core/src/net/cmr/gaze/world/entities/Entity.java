package net.cmr.gaze.world.entities;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.util.Normalize;
import net.cmr.gaze.util.UuidUtils;
import net.cmr.gaze.world.Chunk;
import net.cmr.gaze.world.SpeedChangeTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.World;

/*
 
 ENTITIES THROUGH PACKETS:
 
 X - When an npc enters or leaves a chunk that a player has loaded, send a spawnentity or despawnentity packet respectively
 X - For all npcs in the loaded range of the player, send their UUIDs
 
 - When NPC changes chunks, send the needed spawnEntity or despawnEntity packets to players in the world
 - NPC AND Player position data is sent in an entityPositionPacket, containing a list of data for each entity: 
 	[ entity UUID ] [ entity position x and y]
   
   Any entity outside of the render distance will not be sent in the entity position packet.
   
 - Players are only added and removed when players join/leave the game or world.
 - When ADDING players, make sure any players outside of the render distance has their position sent to the client as the integer limit.
 
*/


public abstract class Entity {
	
	public static final double DELTA_TIME = 1/60d;
	
	double x, y, lastX, lastY, velocityX, velocityY;
	UUID uuid;
	int renderLayer;
	World world;
	EntityType entityType;
	
	public enum EntityType {
		Player,
		DroppedItem,
		NPC,
		Particle,
	}
	
	public Entity(EntityType entityType) {
		this.entityType = entityType;
		x = 0;
		y = 0;
		renderLayer = 1;
		uuid = UUID.randomUUID();
	}
	
	public Entity(EntityType entityType, double x, double y) {
		this.entityType  = entityType;
		this.x = x;
		this.y = y;
		this.lastX = x;
		this.lastY = y;
		renderLayer = 1;
		uuid = UUID.randomUUID();
	}
	
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getVelocityX() {
		return velocityX;
	}
	public double getVelocityY() {
		return velocityY;
	}
	
	double lastServerX, lastServerY, lastServerVelX, lastServerVelY;
	// The methods below are used to negate the effects of interpolation during rendering
	public double lastServerX() {
		return lastServerX;
	}
	public double lastServerY() {
		return lastServerY;
	}
	public double lastServerVelX() {
		return lastServerVelX;
	}
	public double lastServerVelY() {
		return lastServerVelY;
	}
	public void setLastServerData(double x, double y, double velx, double vely) {
		this.lastServerVelX = velx;
		this.lastServerVelY = vely;
		this.lastServerX = x;
		this.lastServerY = y;
	}
	
	public void setPosition(double x, double y) {
		this.lastX = this.x;
		this.lastY = this.y;
		this.x = x;
		this.y = y;
	}
	public void setPositionKeepLast(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public void setVelocity(double velx, double vely) {
		this.velocityX = velx;
		this.velocityY = vely;
	}
	
	public void render(Gaze game, GameScreen screen) {
		if(Gaze.HITBOXES) {
			if (getBoundingBox()!=null) {
				game.batch.draw(game.getSprite("hitbox"), getBoundingBox().getX(), getBoundingBox().getY(), getBoundingBox().width, getBoundingBox().height);
			}
		}
	}
	
	public void update(double deltaTime, TileData data) {
		lastX = x;
		lastY = y;
		
		speedChanges(deltaTime, data);
		//if(data.isServer()) {
		collision(deltaTime, data);
		//}
		
		//System.out.println(deltaTime+":"+data.isServer());
		x += velocityX*deltaTime*Tile.TILE_SIZE;
		y += velocityY*deltaTime*Tile.TILE_SIZE;
	}
	
	public int getRenderLayer() {
		return renderLayer;
	}
	public int getRenderYOffset() {
		return 0;
	}
	public UUID getUUID() {
		return uuid;
	}
	
	public Point getTileCoordinates() {
		//return getTileCoordinates(position);
		return getTileCoordinates(x, y);
	}
	public static Point getTileCoordinates(double x, double y) {
		return new Point((int) (Math.floor(x/Tile.TILE_SIZE)), (int) (Math.floor(y/Tile.TILE_SIZE)));
	}
	
	/**
	 * @return the amount of tiles between the entity and the specified tile coordinates
	 */
	public double getDistanceToTile(int tx, int ty) {
		double distance = getTileCoordinates().distance(tx, ty);
		return distance;
	}
	
	public static int snapPositionToTile(double v) {
		return (int) (Math.floor(v/Tile.TILE_SIZE));
	}
	
	public int getTileX() {
		return snapPositionToTile(x);
	}
	public int getTileY() {
		return snapPositionToTile(y);
	}
	public int getLastTileX() {
		return snapPositionToTile(lastX);
	}
	public int getLastTileY() {
		return snapPositionToTile(lastY);
	}
	/*public static Point getTileCoordinates(Vector2 position) {
		return new Point((int) (Math.floor(position.x/Tile.TILE_SIZE)), (int) (Math.floor(position.y)/Tile.TILE_SIZE));
	}*/
	public Point getChunk() {
		return Chunk.getChunk(getTileX(), getTileY());
	}
	public static Point getChunk(double x, double y) {
		return Chunk.getChunk(snapPositionToTile(x), snapPositionToTile(y));
	}
	public Point getLastChunk() {
		return Chunk.getChunk(getLastTileX(), getLastTileY());
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	public World getWorld() {
		return world;
	}
	
	public void speedChanges(double deltaTime, TileData data) {
		
		Tile underneath = data.getTile(getTileX(), getTileY(), 0);
		if(underneath instanceof SpeedChangeTile) {
			SpeedChangeTile tile = (SpeedChangeTile) underneath;
			velocityX *= tile.getSpeedMultiplier();
			velocityY *= tile.getSpeedMultiplier();
		}
	}
	
	public void collision(double deltaTime, TileData data) {
		
		// dont do collision checks for objects without a collision hitbox
		if(getBoundingBox()==null) {
			return;
		}
		
		double afterX = x+velocityX*deltaTime*Tile.TILE_SIZE, afterY = y+velocityY*deltaTime*Tile.TILE_SIZE;
		
		ArrayList<Rectangle> tileBoxes = new ArrayList<>();
		int radius = 2;
		
		final int xCenter = getTileX(), yCenter = getTileY();
		
		for(int x = xCenter-radius; x <= xCenter+radius; x++) {
			for(int y = yCenter-radius; y <= yCenter+radius; y++) {
				Tile at = data.getTile(x, y, 1);
				if(at == null) {
					continue;
				}
				Rectangle box = at.getBoundingBox(x, y);
				if(box != null) {
					tileBoxes.add(box);
				}
			}
		}
		
		double deltaX = velocityX*deltaTime*Tile.TILE_SIZE, deltaY = velocityY*deltaTime*Tile.TILE_SIZE;
		
		Rectangle before = getBoundingBox(this, getX(), getY());
		Rectangle entityHor = getBoundingBox(this, getX()+deltaX, getY());
		Rectangle entityVer = getBoundingBox(this, getX(), getY()+deltaY);
		

		for(Rectangle tileBox : tileBoxes) {
			if(before.overlaps(tileBox)) {
				velocityY = 0;
				velocityX = 0;
				return;
			}
		}
		
		for(Rectangle tileBox : tileBoxes) {
			if(entityHor.overlaps(tileBox)) {
				// horizontal collision detected, update deltaX accordingly
				deltaX = 0;
				entityHor = getBoundingBox(this, getX()+deltaX, getY());
			}
			if(entityVer.overlaps(tileBox)) {
				// vertical collision detected, update deltaY accordingly
				deltaY = 0;
				entityVer = getBoundingBox(this, getX(), getY()+deltaY);
			}
		}
		Rectangle entityBoth = getBoundingBox(this, getX()+deltaX, getY()+deltaY);
		for(Rectangle tileBox : tileBoxes) {
			if(entityBoth.overlaps(tileBox)) {
				// corner collision detected, update both deltaX and deltaY accordingly
				deltaX = 0;
				deltaY = 0;
				entityBoth = getBoundingBox(this, getX()+deltaX, getY()+deltaY);
			}
		}
		
		velocityX = deltaX/deltaTime/Tile.TILE_SIZE;
		velocityY = deltaY/deltaTime/Tile.TILE_SIZE;
		
		//velocityX -= Normalize.norm(velocityX)*deltaTime;
		//velocityY -= Normalize.norm(velocityY)*deltaTime;
	}
	
	public Rectangle getBoundingBox() {
		return null;
	}
	public Rectangle getBoundingBox(Entity entity, double x, double y) {
		Rectangle entityBox = entity.getBoundingBox();
		if(entityBox == null) {
			return entityBox;
		}
		entityBox.setPosition((float) x, (float) y);
		return entityBox;
	} 
	
	final int VERSION = 0;
	
	public void writeEntity(DataBuffer buffer, boolean obfuscatePosition, boolean toFile) throws IOException {
		buffer.writeInt(VERSION);
		buffer.writeInt(UuidUtils.asBytes(uuid).length);
		buffer.write(UuidUtils.asBytes(uuid));
		if(obfuscatePosition) {
			buffer.writeDouble(Double.MAX_VALUE);
			buffer.writeDouble(Double.MAX_VALUE);
		} else {
			buffer.writeDouble(x);
			buffer.writeDouble(y);
		}
		buffer.writeInt(entityType.ordinal());
	}
	
	public static Entity readEntity(DataInputStream input, boolean fromFile) throws IOException {
		int version = input.readInt();
		byte[] uuidBytes = new byte[input.readInt()];
		input.read(uuidBytes);
		UUID id = UuidUtils.asUuid(uuidBytes);

		double x = input.readDouble();
		double y = input.readDouble();
		
		EntityType type = EntityType.values()[input.readInt()];

		Entity returned = null;
		
		switch(type) {
		case Player: {
			returned = new Player().readEntityData(input, fromFile);
			break;
		}
		case DroppedItem: {
			returned = new DroppedItem().readEntityData(input, fromFile);
			break;
		}
		case NPC: {
			returned = new NPC().readEntityData(input, fromFile);
			break;
		}
		case Particle: {
			returned = new Particle().readEntityData(input, fromFile);
			break;
		}
		}
		
		returned.uuid = id;
		returned.x = x;
		returned.y = y;
		return returned;
	}
	
	public void deleteEntity() {
		getWorld().removeEntity(this);
	}
	
	protected abstract Entity readEntityData(DataInputStream input, boolean fromFile) throws IOException;
	
	@Override
	public int hashCode() {
		return Objects.hash(uuid, entityType);
	}
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof Entity) {
			Entity e = (Entity) obj;
			if(e.entityType==this.entityType) {
				if(e.getUUID().equals(this.getUUID())) {
					return true;
				}
			}
		}
		
		return false;
	}
}

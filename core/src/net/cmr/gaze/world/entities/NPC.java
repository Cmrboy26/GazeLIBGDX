package net.cmr.gaze.world.entities;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.pathfind.AStar;
import net.cmr.gaze.world.pathfind.DirectWalk;

public class NPC extends Entity {

	public NPC() {
		super(EntityType.NPC);
	}

	public NPC(double x, double y) {
		super(EntityType.NPC, x, y);
	}
	
	@Override
	protected Entity readEntityData(DataInputStream input, boolean fromFile) throws IOException {
		return this;
	}
	
	@Override
	public void writeEntity(DataBuffer buffer, boolean obfuscatePosition, boolean toFile) throws IOException {
		super.writeEntity(buffer, obfuscatePosition, toFile);
	}
	
	float stateTime = 0;
	String lastAnimation;
	
	double moveDelta = 0;
	Vector2 movement;
	int tileX = -1, tileY = -1;
	
	Point targetTile;

	@Override
	public void update(double deltaTime, TileData data) {
		super.update(deltaTime, data);
		if(tileX == -1) {
			tileX = 0+7;
			tileY = 0;
		}
		if(data.isServer()) {
			moveDelta+=deltaTime;
			if(moveDelta>.2f) {
				moveDelta=0;
				
				//movement = AStar.findMovementVector(this, data, 1, new Point(tileX, tileY));

				targetTile = AStar.moveToTile(this, data, 1, new Point(tileX, tileY));

				//System.out.println(movement);
				
				
			}
			movement = DirectWalk.walkDirectlyTowards(this, targetTile);
			if(movement != null) {
				this.setVelocity(-movement.x, -movement.y);
			}
		}
	}
	
	public Rectangle getBoundingBox() {
		return new Rectangle((float) (getX()-Tile.TILE_SIZE/2+Tile.TILE_SIZE/6), (float) (getY()), Tile.TILE_SIZE-Tile.TILE_SIZE/3, Tile.TILE_SIZE/4);
	}
	
	@Override
	public void render(Gaze game, GameScreen screen) {
		if(!getAnimationString().equals(lastAnimation)) {
			lastAnimation = getAnimationString();
			stateTime = 0;
		}
		stateTime += Gdx.graphics.getDeltaTime();
		game.batch.draw(game.getAnimation(getAnimationString()).getKeyFrame(stateTime, true), (float) (getX()-Tile.TILE_SIZE/2-Tile.TILE_SIZE/2), (float) (getY()-Tile.TILE_SIZE/2), Tile.TILE_SIZE*2, Tile.TILE_SIZE*2);
		super.render(game, screen);
	}
	
	String lastDirection;
	
	public String getAnimationString() {
		
		float threshold = .5f;
		
		boolean moving = false;

		if(getVelocityY() >= threshold) {
			moving = true;
			lastDirection = "Up";
		} else if(getVelocityY() <= -threshold) {
			moving = true;
			lastDirection = "Down";
		}
		if(getVelocityX() >= threshold) {
			moving = true;
			lastDirection = "Right"; 
		} else if(getVelocityX() <= -threshold) {
			moving = true;
			lastDirection = "Left";
		}
		
		if(lastDirection==null) {
			lastDirection = "Down";
		}
		
		return "player"+(moving?"Walk":"Idle")+lastDirection+"1";
		
	}
	
}

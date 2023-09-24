package net.cmr.gaze.world.entities;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.util.CustomMath;
import net.cmr.gaze.world.HealthEntityListener;
import net.cmr.gaze.world.World;

public abstract class HealthEntity extends Entity {

	int health;
	HealthEntityListener listener;
	
	public HealthEntity(EntityType entityType, HealthEntityListener listener) {
		super(entityType);
		this.listener = listener;
		if(listener==null) {
			this.listener = World.HEALTH_ENTITY_LISTENER;
		}
		health = getMaxHealth();
	}
	
	public HealthEntity(EntityType entityType, double x, double y, HealthEntityListener listener) {
		super(entityType, x, y);
		this.listener = listener;
		if(listener==null) {
			this.listener = World.HEALTH_ENTITY_LISTENER;
		}
		health = getMaxHealth();
	}
	
	public void setHealth(int health) {
		this.health = health;
		if(listener!=null) {
			listener.healthChanged(this, health);
		}
		if(this.health <= 0) {
			onDeath();
		}
	}
	
	public void damage(int health) {
		this.health-=health;
		if(this.health<0) {
			this.health = 0;
		}
		if(listener!=null) {
			listener.healthChanged(this, health);
		}
		if(this.health <= 0) {
			onDeath();
		}
	}
	
	public void heal(int health) {
		this.health+=health;
		if(this.health>getMaxHealth()) {
			this.health = getMaxHealth();
		}
		if(listener!=null) {
			listener.healthChanged(this, -health);
		}
		if(this.health <= 0) {
			onDeath();
		}
	}
	
	public void addListener(HealthEntityListener listener) {
		this.listener = listener;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getMaxHealth() {
		return 100;
	}
	
	public void onDeath() {
		
	}
	
	@Override
	public void writeEntity(DataBuffer buffer, boolean obfuscatePosition, boolean toFile) throws IOException {
		super.writeEntity(buffer, obfuscatePosition, toFile);
		writeHealthEntity(buffer, obfuscatePosition, toFile);
		buffer.writeInt(health);
	}
	
	@Override
	protected Entity readEntityData(DataInputStream input, boolean fromFile) throws IOException {
		HealthEntity entity = readHealthEntityData(input, fromFile);
		entity.health = input.readInt();
		return entity;
	}
	
	public abstract HealthEntity readHealthEntityData(DataInputStream input, boolean fromFile) throws IOException;
	public abstract void writeHealthEntity(DataBuffer buffer, boolean obfuscatePosition, boolean toFile) throws IOException;

}

package net.cmr.gaze.world.entities;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.util.CustomMath;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.Tiles;

public class Particle extends Entity {

	int offsetY;
	float particleLife;
	float particleLifeSpan;
	ParticleEffectType type;
	boolean effectStarted;
	Object source;
	
	LinkedList<ParticleData> particleList;
	
	class ParticleData {
		float x, y, vx, vy;
		public ParticleData(float x, float y, float vx, float vy) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
		}
		
		float ax, ay;
		public void setAcceleration(float ax, float ay) {
			this.ax = ax;
			this.ay = ay;
		}
		
		public void update(float deltaTime) {
			vx+=deltaTime*ax;
			vy+=deltaTime*ay;
			x+=deltaTime*vx;
			y+=deltaTime*vy;
		}
		
	}
	
	public enum ParticleEffectType {
		BREAK(.8f),
		LEAVES(2.25f);
		
		
		public float lifeSpan;
		private ParticleEffectType(float lifeSpan) {
			this.lifeSpan = lifeSpan;
		}
	}
	
	public static Particle createParticle(float x, float y, ParticleEffectType type, float lifeSpan, float offsetY, Object source) {
		Particle p = new Particle();
		p.setPosition(x*Tile.TILE_SIZE, y*Tile.TILE_SIZE);
		p.type = type;
		p.particleLife = type.lifeSpan;
		p.particleLifeSpan = type.lifeSpan;
		p.offsetY = (int) (offsetY*Tile.TILE_SIZE);
		p.source = source;
		return p;
	}
	
	public Particle() {
		super(EntityType.Particle);
	}
	
	@Override
	public void render(Gaze game, GameScreen screen) {
		
		switch(type) {
		case BREAK:
			Color color = Tiles.getAverageColor(((Tile)source).getType());
			if(!effectStarted) {
				particleList = new LinkedList<>();
				
				if(color == null) {
					effectStarted = true;
					return;
				}
				
				Random random = new Random();
				for(int i = 0; i < 10; i++) {
					ParticleData data = new ParticleData((random.nextFloat()-.5f)/2f+.5f, (random.nextFloat()-.5f)/2f+.5f, (random.nextFloat()-.5f)/1.5f, (random.nextFloat()/2f));
					data.setAcceleration(0, -1.2f);
					particleList.add(data);
				}
				
				effectStarted = true;
			}
			
			float delta = Gdx.graphics.getDeltaTime()*4;
			
			if(color == null) {
				return;
			}

			float alpha = (float) CustomMath.minMax(0f, particleLife*10f, 1f);
			
			game.batch.setColor(color.r, color.g, color.b, alpha);
			for(ParticleData data : particleList) {
				data.update(delta);
				if(data.vy < -1.2f-(offsetY/2f/Tile.TILE_SIZE)) {
					data.vx = 0;
					data.vy = 0;
					data.setAcceleration(0, 0);
				}
				
				game.batch.draw(game.getSprite("particle"), (float) getX()+data.x*Tile.TILE_SIZE, (float) getY()+data.y*Tile.TILE_SIZE+offsetY, Tile.TILE_SIZE/10f, Tile.TILE_SIZE/10f);
				
			}
			game.batch.setColor(Color.WHITE);
			
			break;
		case LEAVES: {
			color = Color.GREEN;
			float scale = 1f;
			if(!effectStarted) {
				particleList = new LinkedList<>();
				
				if(color == null) {
					effectStarted = true;
					return;
				}
				
				Random random = new Random();
				for(int i = 0; i < 20; i++) {
					ParticleData data = new ParticleData((random.nextFloat()-.25f), (random.nextFloat()*1.25f), (random.nextFloat())/4.5f, -.2f-(random.nextFloat()/6f));
					particleList.add(data);
				}
				
				effectStarted = true;
			}
			
			delta = Gdx.graphics.getDeltaTime()*2;

			alpha = (float) CustomMath.minMax(0f, particleLife*10f, 1f);
			float fadeInScale = CustomMath.minMax(0, getElapsedTime()*3f, 1f);
			alpha = alpha*fadeInScale;
			
			game.batch.setColor(color.r, color.g, color.b, alpha);
			for(ParticleData data : particleList) {
				data.update(delta);
				if(data.vy < -1.2f-(offsetY/2f/Tile.TILE_SIZE)) {
					data.vx = 0;
					data.vy = 0;
				}
				
				game.batch.draw(game.getAnimation("leaf_particles").getKeyFrame(getElapsedTime()+Math.abs((data.vx+data.vy)*10f)), (float) getX()+data.x*Tile.TILE_SIZE, (float) getY()+data.y*Tile.TILE_SIZE+offsetY, Tile.TILE_SIZE/scale, Tile.TILE_SIZE/scale);
				
			}
			game.batch.setColor(Color.WHITE);
			
			break;
		}
		default:
			break;
		}
		
		super.render(game, screen);
	}
	
	public float getElapsedTime() {
		return particleLifeSpan-particleLife;
	}
	
	@Override
	public void update(double deltaTime, TileData data) {
		particleLife-=deltaTime;
		if(data.isServer()) {
			if(particleLife<0) {
				deleteEntity();
				return;
			}
		}
	}
	
	@Override
	public void writeEntity(DataBuffer buffer, boolean obfuscatePosition, boolean toFile) throws IOException {
		super.writeEntity(buffer, obfuscatePosition, toFile);
		buffer.writeInt(type.ordinal());
		buffer.writeFloat(particleLife);
		buffer.writeFloat(particleLifeSpan);
		buffer.writeInt(offsetY);
		switch(type) {
		case BREAK:
			Tile.writeOutgoingTile((Tile)source, buffer);
			break;
		case LEAVES: {
			break;
		}
		default:
			break;
		
		}
	}
	
	@Override
	protected Entity readEntityData(DataInputStream input, boolean fromFile) throws IOException {
		Particle particle = new Particle();
		particle.type = ParticleEffectType.values()[input.readInt()];
		particle.particleLife = input.readFloat();
		particle.particleLifeSpan = input.readFloat();
		particle.offsetY = input.readInt();
		switch(particle.type) {
		case BREAK:
			particle.source = Tile.readIncomingTile(input);
			break;
		case LEAVES:
			break;
		default:
			break;
		
		}
		return particle;
	}
	
}

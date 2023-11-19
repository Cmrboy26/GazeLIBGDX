package net.cmr.gaze.world;

import com.badlogic.gdx.graphics.g3d.particles.ParticleShader.ParticleType;

import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.leveling.Skills.Skill;
import net.cmr.gaze.world.entities.DroppedItem;
import net.cmr.gaze.world.entities.Particle;
import net.cmr.gaze.world.entities.Particle.ParticleEffectType;
import net.cmr.gaze.world.entities.Player;

public class TileUtils {

	/**
	 * Drops an item on the ground at the specified location in the world.
	 * @param world the world to drop the item in
	 * @param tileX the tile x-coordinate of the location to drop the item
	 * @param tileY the tile y-coordinate of the location to drop the item
	 * @param itemToDrop the item to drop
	 */
	public static void dropItem(World world, int tileX, int tileY, Item itemToDrop) {
		world.addEntity(new DroppedItem(itemToDrop, tileX, tileY));
	}
	
	/**
	 * @return an integer that can be used to show how much a tile has been broken.
	 * 
	 * For example, if you want to slowly display cracks as you break more, you can create
	 * (an example) 5 breaking sprites with names: tile1, tile2, tile3, tile4, and tile5
	 * with tile0 being the default sprite and 5 being the most broken one.
	 * 
	 * You could then call getBreakSpriteInt(this, 5) and get the sprite that should be displayed
	 */
	public static int getBreakSpriteInt(Tile tile, int breakSpriteAmount) {
		
		float max = tile.getType().breakAmount;
		double progress = (double) tile.getBreakAmount()/max;
		
		int sprite = (int) Math.floor(((1f-progress)*(breakSpriteAmount)))+1;
		return sprite;
		
	}
	
	public static void addPlayerXP(Player player, World world, Skill skill, float xp) {
		if(player == null) {
			return;
		}
		player.addXP(world, skill, xp);
	}
	
	public static void spawnParticleOffset(World world, ParticleEffectType type, Tile tile, float x, float y, float offsetY, Object... data) {
		world.addEntity(Particle.createParticle(x, y, type, .8f, offsetY, data));
	}
	public static void spawnParticle(World world, ParticleEffectType type, Tile tile, float x, float y, Object... data) {
		world.addEntity(Particle.createParticle(x, y, type, .8f, 0, data));
	}
	public static void spawnBreakParticleOffset(World world, Tile tile, float x, float y, float offsetY, Object... data) {
		world.addEntity(Particle.createParticle(x, y, ParticleEffectType.BREAK, .8f, offsetY, data));
	}
	public static void spawnBreakParticle(World world, Tile tile, float x, float y, Object... data) {
		world.addEntity(Particle.createParticle(x, y, ParticleEffectType.BREAK, .8f, 0, data));
	}
	
}

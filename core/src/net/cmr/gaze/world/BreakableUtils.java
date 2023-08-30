package net.cmr.gaze.world;

import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.leveling.Skills.Skill;
import net.cmr.gaze.world.entities.DroppedItem;
import net.cmr.gaze.world.entities.Particle;
import net.cmr.gaze.world.entities.Particle.ParticleEffectType;
import net.cmr.gaze.world.entities.Player;

public class BreakableUtils {

	public static void dropItem(World world, int x, int y, Item item) {
		world.addEntity(new DroppedItem(item, x, y));
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
		
		int max = tile.getType().breakAmount;
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
	
	public static void spawnParticle(World world, Tile tile, float x, float y, float offsetY, Object source) {
		world.addEntity(Particle.createParticle(x, y, ParticleEffectType.BREAK, .8, offsetY, source));
	}
	public static void spawnParticle(World world, Tile tile, float x, float y, Object source) {
		world.addEntity(Particle.createParticle(x, y, ParticleEffectType.BREAK, .8, 0, source));
	}
	
}

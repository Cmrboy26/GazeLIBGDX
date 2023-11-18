package net.cmr.gaze.world;

import com.badlogic.gdx.graphics.Color;

public interface LightSource {

	public float getIntensity();
	
	/**
	 * The X offset (in tiles) from the center of the tile
	 * @return the offset
	 */
	public default float offsetX() {return 0;}
	/**
	 * The Y offset (in tiles) from the center of the tile
	 * @return the offset
	 */
	public default float offsetY() {return 0;}

	public default Color getColor() {return Color.WHITE;}
	
}

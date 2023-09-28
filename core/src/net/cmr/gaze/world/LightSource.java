package net.cmr.gaze.world;

import com.badlogic.gdx.graphics.Color;

public interface LightSource {

	public float getIntensity();
	
	public default float offsetX() {return 0;}
	public default float offsetY() {return 0;}

	public default Color getColor() {return Color.WHITE;}
	
}

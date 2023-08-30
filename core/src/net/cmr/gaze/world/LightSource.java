package net.cmr.gaze.world;

public interface LightSource {

	public float getIntensity();
	
	public default float offsetX() {return 0;}
	public default float offsetY() {return 0;}
	
}

package net.cmr.gaze.world.interfaceTiles;

public interface SpeedChangeTile {

	public float getSpeedMultiplier();
	public default void onEnter() {}
	public default void onExit() {}
	
}

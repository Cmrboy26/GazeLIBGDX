package net.cmr.gaze.world;

public abstract class RotatableTile extends Tile implements Rotatable{

	public RotatableTile(TileType tileType) {
		super(tileType);
	}
	
	int direction;
	@Override
	public int getDirection() {
		return direction;
	}
	@Override
	public void setDirection(int v) {
		direction = v;
	}
	
}

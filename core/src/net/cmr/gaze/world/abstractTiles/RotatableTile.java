package net.cmr.gaze.world.abstractTiles;

import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.interfaceTiles.Rotatable;

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

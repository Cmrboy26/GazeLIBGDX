package net.cmr.gaze.inventory;

import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.world.Rotatable;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;

public abstract class Placeable extends Item {

	public static Tile temporaryPlaceTile;
	
	public Placeable(ItemType type, int amount) {
		super(type, amount);
	}
	
	public abstract TileType getTileToPlace();
	public Tile getTempPlaceTile(int rotation) {
		
		if(temporaryPlaceTile!=null) {
			if(temporaryPlaceTile.getType()==getTileToPlace()) {
				if(temporaryPlaceTile instanceof Rotatable) {
					if(((Rotatable)temporaryPlaceTile).getDirection()==rotation) {
						return temporaryPlaceTile;
					}
				} else {
					return temporaryPlaceTile;
				}
			}
		}
		
		temporaryPlaceTile = getPlaceTile(rotation);
		return temporaryPlaceTile;
	}
	
	public Tile getPlaceTile(int rotation) {
		Tile tile = Tiles.getTile(getTileToPlace());
		if(tile instanceof Rotatable) {
			((Rotatable)tile).setDirection(rotation);
		}
		return tile;
	}
	
	public String getPlaceAudio() {
		return "place";
	}
	
}

package net.cmr.gaze.inventory;

import net.cmr.gaze.inventory.Items.ItemType;

public abstract class SeedItem extends Placeable {

	public SeedItem(ItemType type, int size) {
		super(type, size);
	}
	
	@Override
	public String getPlaceAudio() {
		return "grassBreak";
	}

}

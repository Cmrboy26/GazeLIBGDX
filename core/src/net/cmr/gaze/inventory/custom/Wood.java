package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class Wood extends BasicItem {

	public Wood(int size) {
		super(ItemType.WOOD, size, "wood");
	}

	@Override
	public Item getItem(int size) {
		return new Wood(size);
	}

}

package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class Wheat extends BasicItem {

	public Wheat(int size) {
		super(ItemType.WHEAT, size, "wheat");
	}

	@Override
	public Item getItem(int size) {
		return new Wheat(size);
	}

}

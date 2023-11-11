package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class Cotton extends BasicItem {

	public Cotton(int size) {
		super(ItemType.COTTON, size, "cotton");
	}

	@Override
	public Item getItem(int size) {
		return new Cotton(size);
	}
}

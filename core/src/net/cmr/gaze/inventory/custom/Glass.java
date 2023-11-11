package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class Glass extends BasicItem {

    public Glass(int size) {
        super(ItemType.GLASS, size, "glass");
    }

    @Override
    public Item getItem(int size) {
        return new Glass(size);
    }

}

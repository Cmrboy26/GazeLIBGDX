package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class ClayItem extends BasicItem {

    public ClayItem(int size) {
        super(ItemType.CLAY, size, "clayItem");
    }

    @Override
    public Item getItem(int size) {
        return new ClayItem(size);
    }

}

package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class BrickItem extends BasicItem {

    public BrickItem(int size) {
        super(ItemType.BRICK, size, "brick");
    }

    @Override
    public Item getItem(int size) {
        return new BrickItem(size);
    }

}

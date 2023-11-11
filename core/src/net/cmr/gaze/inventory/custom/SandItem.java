package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class SandItem extends BasicItem {

    public SandItem(int size) {
        super(ItemType.SAND, size, "sandItem");
    }

    @Override
    public Item getItem(int size) {
        return new SandItem(size);
    }

}

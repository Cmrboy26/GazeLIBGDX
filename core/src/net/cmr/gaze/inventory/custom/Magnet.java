package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class Magnet extends BasicItem {

    public Magnet(int size) {
        super(ItemType.MAGNET, size, "magnet");
    }

    @Override
    public Item getItem(int size) {
        return new Magnet(size);
    }
    
}

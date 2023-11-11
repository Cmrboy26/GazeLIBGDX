package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class SteelIngot extends BasicItem {

    public SteelIngot(int size) {
        super(ItemType.STEEL_INGOT, size, "steelIngot");
    }

    @Override
    public Item getItem(int size) {
        return new SteelIngot(size);
    }
    
}

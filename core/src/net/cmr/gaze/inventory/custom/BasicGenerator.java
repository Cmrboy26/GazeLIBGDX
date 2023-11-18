package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class BasicGenerator extends BasicItem {

    public BasicGenerator(int size) {
        super(ItemType.BASIC_GENERATOR, size, "basicGenerator");
    }

    @Override
    public Item getItem(int size) {
        return new BasicGenerator(size);
    }
    
}

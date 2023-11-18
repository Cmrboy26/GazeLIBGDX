package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class BasicMotor extends BasicItem {

    public BasicMotor(int size) {
        super(ItemType.BASIC_MOTOR, size, "basicMotor");
    }

    @Override
    public Item getItem(int size) {
        return new BasicMotor(size);
    }
    
}

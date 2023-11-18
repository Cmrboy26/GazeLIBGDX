package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class BasicCircuit extends BasicItem {

    public BasicCircuit(int size) {
        super(ItemType.BASIC_CIRCUIT, size, "basicCircuitBoard");
    }

    @Override
    public Item getItem(int size) {
        return new BasicCircuit(size);
    }
    
}

package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class WaterCanister extends BasicItem {

    public WaterCanister(int size) {
        super(ItemType.WATER_CANISTER, size, "waterCanister");
    }

    @Override
    public Item getItem(int size) {
        return new WaterCanister(size);
    }
    
}

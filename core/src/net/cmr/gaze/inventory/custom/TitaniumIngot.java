package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class TitaniumIngot extends BasicItem {

    public TitaniumIngot(int size) {
        super(ItemType.TITANIUM_INGOT, size, "titaniumIngot");
    }

    @Override
    public Item getItem(int size) {
        return new TitaniumIngot(size);
    }

}

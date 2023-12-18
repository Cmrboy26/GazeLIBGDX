package net.cmr.gaze.inventory.custom;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class SteamCanister extends BasicItem {

    public SteamCanister(int size) {
        super(ItemType.STEAM_CANISTER, size, "steamCanister");
    }

    @Override
    public Item getItem(int size) {
        return new SteamCanister(size);
    }
    
}

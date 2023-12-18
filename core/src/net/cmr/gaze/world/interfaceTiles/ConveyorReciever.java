package net.cmr.gaze.world.interfaceTiles;

import net.cmr.gaze.inventory.Item;

public interface ConveyorReciever {

    /*
     * Returns true if the conveyor can accept an item.
     * For tiles such as conveyors, this should return true if
     * there is no item on the conveyor.
     */
    public boolean canAcceptItem(Item item);
    public void acceptItem(Item item);

}

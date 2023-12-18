package net.cmr.gaze.world.interfaceTiles;

import net.cmr.gaze.inventory.Item;

public interface ConveyorDepositer {
    
    public default boolean depositItem(ConveyorReciever reciever, Item item) {
        if(reciever.canAcceptItem(item)) {
            reciever.acceptItem(item);
            onDepositItem(reciever, item);
            return true;
        }
        return false;
    }
    public default void onDepositItem(ConveyorReciever reciever, Item item) {
        
    }

}

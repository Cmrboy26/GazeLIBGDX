package net.cmr.gaze.world.interfaceTiles;

import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.world.StructureTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;

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

    public default boolean depositToTile(TileData data, int x, int y, Item item) {
        if(data.isClient()) {
            return false;
        }
        Tile at = data.getTile(x, y, 1);
        if(at instanceof StructureTile) {
            at = ((StructureTile) at).getMultiTileCore(data, x, y);
            System.out.println(at);
        }
        if(at instanceof ConveyorReciever) {
            boolean completed = depositItem((ConveyorReciever) at, item);
            if(completed) {
                data.getServerData().onTileChange(x, y, 1);
                return true;
            }
        }
        return false;
    }

}

package net.cmr.gaze.world.abstractTiles;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.DataBuffer;
import com.badlogic.gdx.utils.Null;

import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.world.LightSource;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileType.TickType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.interfaceTiles.ConveyorDepositer;
import net.cmr.gaze.world.interfaceTiles.ConveyorReciever;
import net.cmr.gaze.world.interfaceTiles.Rotatable;

public abstract class ConveyorTile extends RotatableTile implements ConveyorDepositer, ConveyorReciever, LightSource {

    protected Item item;
    protected float conveyorDelta = 0;

    public ConveyorTile(TileType tileType) {
        super(tileType);
        if(tileType.layer != 1) throw new IllegalArgumentException("ConveyorTile must be on layer 1");
        //if(tileType.type != TickType.CONSTANT) throw new IllegalArgumentException("ConveyorTile must be of type CONSTANT");
    }
    
    @Override
    public void update(TileData data, Point worldCoordinates, boolean updatedByPlayer) {
        // Conveyors should PUSH items, and not PULL them.
        // The conveyorDelta is used to determine how far the item has been moved along the conveyor 
        // and should only be reset when the item is pushed from the conveyor.
        // When the conveyorDelta is greater than 1, the conveyor should
        // scan for a conveyor to push the item to.
        if(data.isServer()) {
            if(item != null) {
                conveyorDelta += getConveyorSpeed()*Tile.DELTA_TIME;
                if(conveyorDelta >= 1) {
                    ConveyorReciever reciever = getReciever(data, worldCoordinates);
                    if(reciever != null && reciever.canAcceptItem(item)) {
                        conveyorDelta = 0;
                        depositItem(reciever, item);
                        data.getServerData().onTileChange(worldCoordinates.x, worldCoordinates.y, 1);
                        data.getServerData().onTileChange((int) (worldCoordinates.x + getComponentX()), (int) (worldCoordinates.y + getComponentY()), 1);
                    }
                }
            }
        }
    }

    

    @Override
    public void onDepositItem(ConveyorReciever reciever, Item item) {
        this.item = null;
    }

    public ConveyorReciever getReciever(TileData data, Point worldCoordinates) {
        Point nextTile = new Point((int) (worldCoordinates.x + getComponentX()), (int) (worldCoordinates.y + getComponentY()));
        Tile tile = data.getTile(nextTile.x, nextTile.y, 1);
        if(tile instanceof ConveyorReciever) {
            return (ConveyorReciever) tile;
        }
        return null;
    }

    public float getConveyorDelta() {
        return conveyorDelta;
    }

    /**
     * Defines the amount of items that can be transported per second.
     * @return items/second
     */
    public abstract float getConveyorSpeed();

    /**
     * Returns the item that is currently on the conveyor.
     * @return the item that is currently on the conveyor
     */
    @Null
    public Item getItem() {
        return item;
    }

    @Override
    public boolean canAcceptItem(Item item) {
        return getItem() == null;
    }
    @Override
    public void acceptItem(Item item) {
        this.item = item;
        this.conveyorDelta = 0;
    }

    @Override
    protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
        writeRotatableData(buffer);
        Item.writeOutgoingItem(item, buffer);
        buffer.writeFloat(conveyorDelta);
    }

    @Override
    public Tile readTile(DataInputStream input, TileType type) throws IOException {
        ConveyorTile tile = (ConveyorTile) Tiles.getTile(type);
        Tile.readBreakData(input, tile);

        Rotatable.readRotatableData(input, tile);
        tile.item = Item.readIncomingItem(input);
        tile.conveyorDelta = input.readFloat();
        return tile;
    }

    @Override
    public void onBreak(World world, Player player, int x, int y) {
        TileUtils.dropItem(world, x, y, getItem());
        TileUtils.dropItem(world, x, y, Items.getItem(ItemType.BASIC_CONVEYOR, 1));
    }

    @Override
    public TileType[] belowWhitelist() {
        return null;
    }

    @Override
    public TileType[] belowBlacklist() {
        return new TileType[] {TileType.LAVA};
    }

    @Override
    public String getBreakNoise() {
        return "stoneHit";
    }
    @Override
    public String getHitNoise() {
        return "stoneHit";
    }

    @Override
    public float getIntensity() {
        if(getItem() instanceof LightSource) {
            return ((LightSource) getItem()).getIntensity();
        }
        return 0;
    }

    @Override
    public Color getColor() {
        if(getItem() instanceof LightSource) {
            return ((LightSource) getItem()).getColor();
        }
        return Color.WHITE;
    }

}

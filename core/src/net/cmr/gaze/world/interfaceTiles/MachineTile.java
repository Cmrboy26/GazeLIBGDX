package net.cmr.gaze.world.interfaceTiles;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.TileType.TickType;
import net.cmr.gaze.world.abstractTiles.ElectricityPole;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.powerGrid.EnergyConsumer;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;
import net.cmr.gaze.world.powerGrid.EnergyProducer;
import net.cmr.gaze.world.powerGrid.EnergyUser;

public interface MachineTile extends EnergyUser {
    
    EnergyDistributor getDistributor();
    void setDistributor(EnergyDistributor distributor);

    Point getDistributorPoint();
    void setDistributorPoint(Point point);

    public boolean getMachineDisplayState();
    public void setMachineDisplayState(boolean on);

    public default void onConstruct(Tile tile) {
        if(tile.getType().tickType != TickType.CONSTANT) throw new IllegalArgumentException("Machine tiles must be constant tick type");
        if(tile.getType().layer != 1) throw new IllegalArgumentException("Machine tiles must be layer 1");
        setDistributorPoint(new Point(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Override
    public default void setEnergyDistributor(EnergyDistributor distributor) {
        if(getEnergyDistributor() != null) {
            getEnergyDistributor().getEnergyUsers().removeUser(this);
        }
        setDistributor(distributor);
        if(getDistributor() != null) {
            distributor.getEnergyUsers().addUser(this);
            setDistributorPoint(((ElectricityPole) distributor).getWorldCoordinates());
        } else {
            setDistributorPoint(new Point(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }
    }

    @Override
    public default void removeEnergyDistributor() {
        if(getDistributor() != null) {
            getDistributor().getEnergyUsers().removeUser(this);
        }
        setDistributor(null);
    }

    public static void writeMachineData(MachineTile tile, DataBuffer buffer) throws IOException {
        if(tile.getDistributorPoint() == null) {
            tile.setDistributorPoint(new Point(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }
        buffer.writeInt(tile.getDistributorPoint().x);
        buffer.writeInt(tile.getDistributorPoint().y);
        buffer.writeInt(tile.getWorldCoordinates().x);
        buffer.writeInt(tile.getWorldCoordinates().y);
        buffer.writeBoolean(tile.getMachineDisplayState());
    }

    public static MachineTile readMachineData(MachineTile tile, DataInputStream input) throws IOException {
        tile.setDistributorPoint(new Point(input.readInt(), input.readInt()));
        tile.setWorldCoordinates(new Point(input.readInt(), input.readInt()));
        tile.setMachineDisplayState(input.readBoolean());
        return tile;
    }

    public default void update(TileData data, Point worldCoordinates) {
        if(data.isServer()) {
            boolean machineProducing = false;
            if(this instanceof EnergyProducer) {
                machineProducing = ((EnergyProducer) this).isMachineProducing();
            } else if(this instanceof EnergyConsumer) {
                machineProducing = ((EnergyConsumer) this).isMachineFunctioning();
            }
            // If the machine has changed state from WORKING <-> NOT WORKING, update the visual of the tile on the client side
            if(machineProducing != getMachineDisplayState()) {
                // Sets the value of the lastDisplayMachineFunctioning variable to the current state of the machine so it can be checked later
                setMachineDisplayState(machineProducing);
                data.getServerData().onTileChange(worldCoordinates.x, worldCoordinates.y, 1);
            }
        }
    }

    public default void overrideOnPlace(World world, int tx, int ty, Player player) {
        setWorldCoordinates(new Point(tx, ty));
        connectToWorld(world, tx, ty);
    }

    public default void generateInitialize(int x, int y, double seed) {
        setWorldCoordinates(new Point(x, y));
    }

    public default void onBreak(World world, Player player, int x, int y) {
        removeEnergyDistributor();
    }

    public default boolean isConnectedToPowerGrid() {
        return getPowerGrid()!=null && getDistributor()!=null;
    }
    
}

package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.RotatableTile;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.interfaceTiles.ConveyorDepositer;
import net.cmr.gaze.world.interfaceTiles.MachineTile;
import net.cmr.gaze.world.interfaceTiles.Rotatable;
import net.cmr.gaze.world.powerGrid.EnergyConsumer;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;

public class BasicPumpTile extends RotatableTile implements MachineTile, EnergyConsumer, ConveyorDepositer {

    float pumpDelta;

    public BasicPumpTile() {
        super(TileType.BASIC_PUMP);
        onConstruct(this);
    }

    @Override
    public Tile readTile(DataInputStream input, TileType type) throws IOException {
        Tile tile = Tiles.getTile(type);
        Tile.readBreakData(input, tile);
        Rotatable.readRotatableData(input, (Rotatable) tile);
        MachineTile.readMachineData((MachineTile) tile, input);
        return tile;
    }

    @Override
    protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
        writeRotatableData(buffer);
        MachineTile.writeMachineData(this, buffer);
    }

    // BOILERPLATE CODE

    @Override
    public void update(TileData data, Point worldCoordinates, boolean updatedByPlayer) {
        if(data.isServer() && isConnectedToPowerGrid()) {
            pumpDelta += Tile.DELTA_TIME*getMachineEfficiency();
        }
        MachineTile.super.update(data, worldCoordinates);
    }

    public void onPlace(World world, int tx, int ty, Player player) {
        MachineTile.super.onPlace(world, tx, ty, player);
    }

    public void generateInitialize(int x, int y, double seed) {
        MachineTile.super.generateInitialize(x, y, seed);
    }

    public void onBreak(World world, Player player, int x, int y) {
        MachineTile.super.onBreak(world, player, x, y);
    }

    EnergyDistributor distributor;
    Point distributorPoint, worldCoordinates;
    boolean machineDisplayState = false;

    @Override public EnergyDistributor getEnergyDistributor() { return distributor; }
    @Override public Point getWorldCoordinates() { return worldCoordinates; }
    @Override public void setWorldCoordinates(Point point) { this.worldCoordinates = point; }
    @Override public EnergyDistributor getDistributor() { return distributor; }
    @Override public void setDistributor(EnergyDistributor distributor) { this.distributor = distributor; }
    @Override public Point getDistributorPoint() { return distributorPoint; }
    @Override public void setDistributorPoint(Point point) { this.distributorPoint = point; }
    @Override public boolean getMachineDisplayState() { return machineDisplayState; }
    @Override public void setMachineDisplayState(boolean on) { this.machineDisplayState = on; }

    @Override
    public TileType[] belowWhitelist() {
        return new TileType[] {TileType.WATER, TileType.LAVA};
    }

    @Override
    public TileType[] belowBlacklist() {
        return null;
    }

    @Override
    public double getEnergyConsumption() {
        return 0;
    }

}

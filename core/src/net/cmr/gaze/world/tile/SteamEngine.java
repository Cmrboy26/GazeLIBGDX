package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.world.BaseTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.ElectricityPole;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;
import net.cmr.gaze.world.powerGrid.EnergyProducer;

public class SteamEngine extends BaseTile implements EnergyProducer {

    EnergyDistributor distributor;
    Point distributorPoint, worldCoordinates;
    boolean lastDisplayMachineProducing = false;
    float steamDelta;
    int steamCount;
    final int MAX_STEAM = 20;
    final float STEAM_TIME = 30;

    public SteamEngine() {
        super(TileType.STEAM_ENGINE, 2, 1);
        distributorPoint = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public void setEnergyDistributor(EnergyDistributor distributor) {
        if(this.distributor != null) {
            this.distributor.getEnergyUsers().removeUser(this);
        }
        this.distributor = distributor;
        if(this.distributor != null) {
            distributor.getEnergyUsers().addUser(this);
            distributorPoint = ((ElectricityPole) distributor).getWorldCoordinates();
        } else {
            distributorPoint = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
    }

    @Override
    public void removeEnergyDistributor() {
        if(this.distributor != null) {
            this.distributor.getEnergyUsers().removeUser(this);
        }
        this.distributor = null;
    }

    @Override
    public EnergyDistributor getEnergyDistributor() {
        return distributor;
    }

    @Override
    public Point getWorldCoordinates() {
        return worldCoordinates;
    }

    @Override
    public void setWorldCoordinates(Point point) {
        this.worldCoordinates = point;
    }

    @Override
    public void generateInitialize(int x, int y, double seed) {
        setWorldCoordinates(new Point(x, y));
    }

    @Override
    public void onPlace(World world, int tx, int ty, Player player) {
        setWorldCoordinates(new Point(tx, ty));
        connectToWorld(world, tx, ty);
    }

    @Override
    public double getEnergyProduced() {
        if(steamCount > 0) {
            return 20f;
        }
        return 0;
    }

    @Override
    public TileType[] belowWhitelist() {
        return null;
    }

    @Override
    public TileType[] belowBlacklist() {
        return Tile.defaultBlacklist;
    }

    @Override
    public Tile readTile(DataInputStream input, TileType type) throws IOException {
        SteamEngine steamEngine = new SteamEngine();
        Tile.readBreakData(input, steamEngine);
        steamEngine.distributorPoint = new Point(input.readInt(), input.readInt());
        steamEngine.worldCoordinates = new Point(input.readInt(), input.readInt());
        steamEngine.steamCount = input.readInt();
        steamEngine.steamDelta = input.readFloat();
        steamEngine.lastDisplayMachineProducing = input.readBoolean();
        return steamEngine;
    }

    @Override
    protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
        if(distributorPoint == null) {
            distributorPoint = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        buffer.writeInt(distributorPoint.x);
        buffer.writeInt(distributorPoint.y);
        buffer.writeInt(worldCoordinates.x);
        buffer.writeInt(worldCoordinates.y);
        buffer.writeInt(steamCount);
        buffer.writeFloat(steamDelta);
        buffer.writeBoolean(lastDisplayMachineProducing);
    }
    
    public boolean equals(Object object) {
        return super.equals(this) && object == this;
    }
}

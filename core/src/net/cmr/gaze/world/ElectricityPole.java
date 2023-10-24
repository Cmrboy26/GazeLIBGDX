package net.cmr.gaze.world;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;
import net.cmr.gaze.world.powerGrid.EnergySubnet;
import net.cmr.gaze.world.powerGrid.PowerGrid;

public abstract class ElectricityPole extends Tile implements EnergyDistributor {

    EnergySubnet subnet;
    PowerGrid grid;
    List<EnergyDistributor> neighbors;

    public ElectricityPole(TileType type) {
        super(type);
        this.subnet = new EnergySubnet();
        this.neighbors = new ArrayList<EnergyDistributor>();
    }

    @Override
    public void onPlace(World world, int x, int y, Player player) {
        // TODO: FIGURE OUT SOMETHING??
    }

    public int getRadius() {
        return 5;
    }

    @Override
    public EnergySubnet getEnergyUsers() {
        return subnet;
    }

    @Override
    public void setPowerGrid(PowerGrid grid) {
        this.grid = grid;
    }

    @Override
    public PowerGrid getPowerGrid() {
        return grid;
    }

    @Override
    public List<EnergyDistributor> getNeighbors() {
        return neighbors;
    }

    @Override
    public void clearNeighbors() {
        neighbors.clear();
    }

    @Override
    public void addNeighbor(EnergyDistributor neighbor) {
        neighbors.add(neighbor);
    }

    @Override
    public void removeNeighbor(EnergyDistributor neighbor) {
        neighbors.remove(neighbor);
    }

    @Override
    public TileType[] belowWhitelist() {
        return null;
    }

    @Override
    public TileType[] belowBlacklist() {
        return defaultBlacklist;
    }

    @Override
    public abstract Tile readTile(DataInputStream input, TileType type) throws IOException;
    @Override
    protected abstract void writeTile(TileType tile, DataBuffer buffer) throws IOException;
    
}

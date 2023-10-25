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
    List<EnergyDistributor> neighbors; // NOTE: These should all be ElectricityPoles or other EnergyDistributor tiles
    Point worldCoordinates;

    public ElectricityPole(TileType type) {
        super(type);
        this.subnet = new EnergySubnet();
        this.neighbors = new ArrayList<EnergyDistributor>();
        this.worldCoordinates = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void onPlace(World world, int x, int y, Player player) {
        this.worldCoordinates = new Point(x, y);
        // Add nearby poles to the neighbors list and this tile to their neighbors list (according to getRadius())
        connectToNetwork(world);
    }

    public void connectToNetwork(World world) {
        ArrayList<EnergyDistributor> tempNeighbors = new ArrayList<>();
        // Search all neighbors within the radius of the pole
        for(int x = this.worldCoordinates.x - getRadius(); x <= this.worldCoordinates.x + getRadius(); x++) {
            for(int y = this.worldCoordinates.y - getRadius(); y <= this.worldCoordinates.y + getRadius(); y++) {
                Tile tile = world.getTile(x, y, 1);
                if(tile instanceof ElectricityPole && tile != this) {
                    tempNeighbors.add((ElectricityPole) tile);
                }
            }
        }
        // Check to see if there is a grid conflict
        // This check will appropriately set the grid of this electric pole AND all of its neighbors
        boolean gridConflict = false;
        PowerGrid gridConflictGrid = null;
        for(EnergyDistributor neighbor : tempNeighbors) {
            if(gridConflictGrid == null) {
                gridConflictGrid = neighbor.getPowerGrid();
            } else if(!gridConflict && !gridConflictGrid.equals(neighbor.getPowerGrid())) {
                gridConflict = true;
            }
            if(gridConflict) {
                // set the largest size grid to be the gridConflictGrid to reduce computation in future steps
                if(gridConflictGrid.getSize() < neighbor.getPowerGrid().getSize()) {
                    gridConflictGrid = neighbor.getPowerGrid();
                }
            }
            addNeighbor(neighbor);
            neighbor.addNeighbor(this);
        }
        if(gridConflict) {
            PowerGrid.setNetworkGrid(gridConflictGrid, this);
        } else {
            setPowerGrid(gridConflictGrid);
        }
    }

    @Override
    public void generateInitialize(int x, int y, double seed) {
        this.worldCoordinates = new Point(x, y);
    }

    public Point getWorldCoordinates() {
        return worldCoordinates;
    }

    public void writeConnections(DataBuffer electricityBuffer) throws IOException {
        electricityBuffer.writeInt(worldCoordinates.x);
        electricityBuffer.writeInt(worldCoordinates.y);
        electricityBuffer.writeInt(neighbors.size());
        for(EnergyDistributor neighbor : neighbors) {
            ElectricityPole pole = (ElectricityPole) neighbor;
            electricityBuffer.writeInt(pole.getWorldCoordinates().x);
            electricityBuffer.writeInt(pole.getWorldCoordinates().y);
        }
    }

    public static void readConnections(DataInputStream in, World world) throws IOException {
        Point worldCoordinates = new Point(in.readInt(), in.readInt());
        // NOTE: no need to set the worldCoordinates of the pole because it will be set when the tile is read from file
        ElectricityPole pole = (ElectricityPole) world.getTile(worldCoordinates.x, worldCoordinates.y, 1);
        int neighborCount = in.readInt();
        for(int i = 0; i < neighborCount; i++) {
            int x = in.readInt();
            int y = in.readInt();
            pole.addNeighbor((ElectricityPole) world.getTile(x, y, 1));
        }
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

    public abstract void writePole(TileType type, DataBuffer buffer) throws IOException;
    public abstract ElectricityPole readPole(DataInputStream input, TileType type) throws IOException;

    @Override
    public Tile readTile(DataInputStream input, TileType type) throws IOException {
        ElectricityPole pole = readPole(input, type);
        pole.worldCoordinates = new Point(input.readInt(), input.readInt());
        return pole;
    }
    @Override
    protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
        if(worldCoordinates == null) {
            throw new NullPointerException("World Coordinate is null for tile "+tile.name());
        }
        writePole(tile, buffer);
        buffer.writeInt(worldCoordinates.x);
        buffer.writeInt(worldCoordinates.y);
    }
    
}

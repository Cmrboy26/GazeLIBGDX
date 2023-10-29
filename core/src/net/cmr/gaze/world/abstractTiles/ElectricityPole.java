package net.cmr.gaze.world.abstractTiles;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;
import net.cmr.gaze.world.powerGrid.EnergySubnet;
import net.cmr.gaze.world.powerGrid.EnergyUser;
import net.cmr.gaze.world.powerGrid.PowerGrid;

public abstract class ElectricityPole extends Tile implements EnergyDistributor {

    EnergySubnet subnet;
    PowerGrid grid;
    List<EnergyDistributor> neighbors; // NOTE: These should all be ElectricityPoles or other EnergyDistributor tiles
    public Point worldCoordinates;

    public ElectricityPole(TileType type) {
        super(type);
        this.worldCoordinates = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        this.neighbors = new ArrayList<EnergyDistributor>();
        this.subnet = new EnergySubnet(this);
    }

    @Override
    public void onPlace(World world, int x, int y, Player player) {
        this.worldCoordinates = new Point(x, y);
        this.neighbors = new ArrayList<EnergyDistributor>();
        this.subnet = new EnergySubnet(this);
        // Add nearby poles to the neighbors list and this tile to their neighbors list (according to getRadius())
        connectToNetwork(world);
    }

    public void connectToNetwork(World world) {
        ArrayList<EnergyDistributor> tempNeighbors = new ArrayList<>();
        // Search all neighbors within the radius of the pole
        for(int x = this.worldCoordinates.x - getRadius(); x <= this.worldCoordinates.x + getRadius(); x++) {
            for(int y = this.worldCoordinates.y - getRadius(); y <= this.worldCoordinates.y + getRadius(); y++) {
                Tile tile = world.getTile(x, y, 1);
                if(tile instanceof ElectricityPole && !(x==this.worldCoordinates.x && y==this.worldCoordinates.y)) {
                    tempNeighbors.add((ElectricityPole) tile);
                }
            }
        }

        for(int x = this.worldCoordinates.x - getRadius(); x <= this.worldCoordinates.x + getRadius(); x++) {
            for(int y = this.worldCoordinates.y - getRadius(); y <= this.worldCoordinates.y + getRadius(); y++) {
                Tile tile = world.getTile(x, y, 1);
                if(tile instanceof EnergyUser) {
                    EnergyUser user = (EnergyUser) tile;

                    if(user.getEnergyDistributor() != null) {
                        // if this distributor is closer than theirs, then replace it
                        float thisDistance = (float) Math.hypot(x - this.worldCoordinates.x, y - this.worldCoordinates.y);
                        float theirDistance = (float) Math.hypot(x - ((ElectricityPole) user.getEnergyDistributor()).getWorldCoordinates().x, y - ((ElectricityPole) user.getEnergyDistributor()).getWorldCoordinates().y);
                        if(thisDistance < theirDistance) {
                            user.setEnergyDistributor(this);
                        }
                    }

                    if(user.getEnergyDistributor() == null) {
                        user.setEnergyDistributor(this);
                    }
                }
            }
        }

        for(EnergyDistributor neighbor : tempNeighbors) {
            EnergyDistributor.connectNodes(this, neighbor);
        }

        PowerGrid.adaptiveSetGrid(this);

        world.onTileChange(worldCoordinates.x, worldCoordinates.y, 1);
        for(EnergyDistributor neighbor : getPowerGrid().getDistributors()) {
            ElectricityPole pole = (ElectricityPole) neighbor;
            world.onTileChange(pole.worldCoordinates.x, pole.worldCoordinates.y, 1);
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
    }

    protected Color DEBUG_COLOR;
    protected int TEST_SIZE;

    public static void readConnections(DataInputStream in, World world) throws IOException {
        Point worldCoordinates = new Point(in.readInt(), in.readInt());
        ElectricityPole pole = (ElectricityPole) world.getTile(worldCoordinates.x, worldCoordinates.y, 1);
        pole.connectToNetwork(world);
    }

    @Override
    public void onBreak(World world, Player player, int x, int y) {
        ArrayList<EnergyDistributor> update = new ArrayList<>(getPowerGrid().getDistributors());
        removeFromGrid();
        ArrayList<EnergyUser> subnet2 = new ArrayList<EnergyUser>(subnet.releaseEnergyUsers());
        for(EnergyUser user : subnet2) {
            Point coords = user.getWorldCoordinates();
            user.removeEnergyDistributor();
            user.connectToWorld(world, coords.x, coords.y);
        }
        for(EnergyDistributor neighbor : update) {
            ElectricityPole pole = (ElectricityPole) neighbor;
            world.onTileChange(pole.worldCoordinates.x, pole.worldCoordinates.y, 1);
        }
    }

    public int getRadius() {
        return 3;
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
        getNeighbors().add(neighbor);
    }

    @Override
    public void removeNeighbor(EnergyDistributor neighbor) {
        getNeighbors().remove(neighbor);
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
    public int hashCode() {
        return worldCoordinates.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ElectricityPole) {
            ElectricityPole pole = (ElectricityPole) obj;
            return pole.worldCoordinates.equals(worldCoordinates);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ElectricityPole["+worldCoordinates.x+", "+worldCoordinates.y+"]";
    }

    public abstract void writePole(TileType type, DataBuffer buffer) throws IOException;
    public abstract ElectricityPole readPole(DataInputStream input, TileType type) throws IOException;

    @Override
    public Tile readTile(DataInputStream input, TileType type) throws IOException {
        ElectricityPole pole = readPole(input, type);
        readBreakData(input, pole);
        pole.worldCoordinates = new Point(input.readInt(), input.readInt());
        
        float f1 = input.readFloat();
        float f2 = input.readFloat();
        float f3 = input.readFloat();
        pole.DEBUG_COLOR = new Color(f1, f2, f3, 1f);
        pole.TEST_SIZE = input.readInt();
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
        // Write 3 random floats based on the hashCode of the powerGrid
        PowerGrid grid = getPowerGrid();
        if(grid == null) {
            buffer.writeFloat(0);
            buffer.writeFloat(0);
            buffer.writeFloat(0);
            buffer.writeInt(-1);
        } else {
            Random r = new Random(grid.hashCode());
            buffer.writeFloat(r.nextFloat());
            buffer.writeFloat(r.nextFloat());
            buffer.writeFloat(r.nextFloat());
            buffer.writeInt(grid.getSize());
        }
    }
    
}

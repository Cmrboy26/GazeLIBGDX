package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.DrillableTile;
import net.cmr.gaze.world.abstractTiles.MultiTile;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.interfaceTiles.ConveyorDepositer;
import net.cmr.gaze.world.interfaceTiles.ExploitableTile;
import net.cmr.gaze.world.interfaceTiles.MachineTile;
import net.cmr.gaze.world.interfaceTiles.Rotatable;
import net.cmr.gaze.world.interfaceTiles.ExploitableTile.ExploitType;
import net.cmr.gaze.world.powerGrid.EnergyConsumer;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;

public class BasicMiningDrill extends MultiTile implements MachineTile, EnergyConsumer, ConveyorDepositer{
 
    float drillDelta;
    transient DrillableTile drillableTile;

    public BasicMiningDrill() {
        super(TileType.BASIC_MINING_DRILL, 2, 1);
        onConstruct(this);
    }

    float renderDelta = 0;

    @Override
    public void render(Gaze game, GameScreen screen, int x, int y) {
        renderDelta += Gdx.graphics.getDeltaTime() * (getMachineDisplayState() ? 1 : 0);
        draw(game.batch, game.getAnimation("steamEngine").getKeyFrame(renderDelta), x, y, 1, 1);
        super.render(game, screen, x, y);
    }

    @Override
    public Tile readTile(DataInputStream input, TileType type) throws IOException {
        Tile tile = Tiles.getTile(type);
        Tile.readBreakData(input, tile);
        MachineTile.readMachineData((MachineTile) tile, input);
        return tile;
    }

    @Override
    protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
        MachineTile.writeMachineData(this, buffer);
    }

    @Override
    public void update(TileData data, Point worldCoordinates, boolean updatedByPlayer) {
        if(drillableTile == null || !((Tile)drillableTile).placedInWorld()) {
            Tile tile = data.getTile((int) (worldCoordinates.x+1), (int) (worldCoordinates.y), 0);
            System.out.println("NEW TILE FOUND: " + tile);
            if(tile instanceof DrillableTile) {
                drillableTile = (DrillableTile) tile;
            }
        }
        if(data.isServer() && isConnectedToPowerGrid() && drillableTile != null) {
            drillDelta += Tile.DELTA_TIME*getMachineEfficiency();
            if(drillDelta >= drillableTile.getDrillTime()) {
                drillDelta = 0;
                depositToTile(data, (int) (worldCoordinates.x+1), (int) (worldCoordinates.y), drillableTile.getExploitedItem());
                /*Tile tile = data.getTile((int) (worldCoordinates.x + getComponentX()), (int) (worldCoordinates.y + getComponentY()), 1);
                if(tile instanceof ConveyorReciever) {
                    ConveyorReciever reciever = (ConveyorReciever) tile;
                    Item itemToAccept = Items.getItem(ItemType.WATER_CANISTER, 1);
                    if(reciever.canAcceptItem(itemToAccept)) {
                        reciever.acceptItem(itemToAccept);
                        data.getServerData().onTileChange((int) (worldCoordinates.x + getComponentX()), (int) (worldCoordinates.y + getComponentY()), 1);
                    }
                }*/
            }
        }
        MachineTile.super.update(data, worldCoordinates);
    }

    public void overrideOnPlace(World world, int tx, int ty, Player player) {
        MachineTile.super.overrideOnPlace(world, tx, ty, player);
    }

    public void overrideGenerateInitialize(int x, int y, double seed) {
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
        return null;
    }

    @Override
    public TileType[] belowBlacklist() {
        return defaultBlacklist;
    }

    @Override
    public double getEnergyConsumption() {
        return 0;
    }


}

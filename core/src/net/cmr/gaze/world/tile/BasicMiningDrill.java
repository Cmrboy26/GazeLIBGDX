package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
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
    transient Tile belowTile;

    public BasicMiningDrill() {
        super(TileType.BASIC_MINING_DRILL, 2, 1);
        onConstruct(this);
    }

    float renderDelta = 0;

    @Override
    public void render(Gaze game, GameScreen screen, int x, int y) {
        renderDelta += Gdx.graphics.getDeltaTime() * (getMachineDisplayState() ? 1 : 0);
        draw(game.batch, game.getAnimation("basicMiningDrill").getKeyFrame(renderDelta* (getMachineDisplayState() ? 1 : 0)), x, y, 2, 1.5f);
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
        
        if(data.isServer()) {
            if(belowTile == null || !belowTile.placedInWorld()) {
                Tile tile = data.getTile((int) (worldCoordinates.x+1), (int) (worldCoordinates.y), 0);
                belowTile = tile;
            }
        }
        if(belowTile instanceof DrillableTile && data.isServer() && isConnectedToPowerGrid() && belowTile != null) {
            DrillableTile drillableTile = (DrillableTile) this.belowTile;
            if(drillDelta < drillableTile.getDrillTime()) {
                drillDelta += Tile.DELTA_TIME*getMachineEfficiency();
            }
            if(drillDelta >= drillableTile.getDrillTime()) {
                drillDelta = 0;
                depositToTile(data, (int) (worldCoordinates.x-1), (int) (worldCoordinates.y), drillableTile.getExploitedItem());
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
        TileUtils.dropItem(world, x, y, Items.getItem(ItemType.BASIC_MINING_DRILL, 1));
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
        return 1;
    }

    @Override
	public String getHitNoise() {
		return "stoneHit";
	}
	@Override
	public String getBreakNoise() {
		return "stoneBreak";
	}

}

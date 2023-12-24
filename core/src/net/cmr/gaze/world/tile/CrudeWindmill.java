package net.cmr.gaze.world.tile;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.EnvironmentController;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.interfaceTiles.MachineTile;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;
import net.cmr.gaze.world.powerGrid.EnergyProducer;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.DataBuffer;

public class CrudeWindmill extends Tile implements MachineTile, EnergyProducer {
    
    float windPower;

    public CrudeWindmill() {
        super(TileType.CRUDE_WINDMILL);
        onConstruct(this);
    }

    float renderDelta = 0;

    @Override
    public void render(Gaze game, GameScreen screen, int x, int y) {

        float displayWind = screen.getEnvironmentController().getWindSpeed()*screen.getEnvironmentController().getWindStrength();

        renderDelta += Gdx.graphics.getDeltaTime() * displayWind;
        draw(game.batch, game.getAnimation("crudeWindmill").getKeyFrame(renderDelta), x-.25f, y, 1.5f, 2);
        super.render(game, screen, x, y);
    }

    @Override
    public void update(TileData data, Point worldCoordinates, boolean updatedByPlayer) {
        EnvironmentController controller = data.getEnvironmentController();
        windPower = controller.getWindSpeed()*controller.getWindStrength();
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
        TileUtils.dropItem(world, x, y, Items.getItem(ItemType.CRUDE_WINDMILL, 1));
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
    public double getEnergyProduced() {
        return windPower;
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

}

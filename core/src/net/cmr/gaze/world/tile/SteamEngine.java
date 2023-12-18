package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.custom.CoalItem;
import net.cmr.gaze.inventory.custom.SteamCanister;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.BaseTile;
import net.cmr.gaze.world.abstractTiles.ElectricityPole;
import net.cmr.gaze.world.entities.Particle;
import net.cmr.gaze.world.entities.Particle.ParticleEffectType;
import net.cmr.gaze.world.interfaceTiles.ConveyorReciever;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;
import net.cmr.gaze.world.powerGrid.EnergyProducer;

public class SteamEngine extends BaseTile implements EnergyProducer, ConveyorReciever {

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

    float renderDelta = 0;

    @Override
    public void render(Gaze game, GameScreen screen, int x, int y) {
        renderDelta += Gdx.graphics.getDeltaTime()* (lastDisplayMachineProducing? 1 : 0);
        game.batch.draw(game.getAnimation("steamEngine").getKeyFrame(renderDelta), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE*2, TILE_SIZE*1.5f);
    }

    float particleDelta = 0;

    @Override
    public void update(TileData data, Point worldCoordinates, boolean loadedByPlayer) {
        if(data.isServer()) {
            if(isMachineProducing() && loadedByPlayer) {
                float speed = ((float) (steamCount) / (float) (MAX_STEAM));
                speed = Interpolation.circleOut.apply(speed);
                particleDelta += Tile.DELTA_TIME*speed;
                if(particleDelta > 1f) {
                    particleDelta = 0;
                    TileUtils.spawnParticleOffset(data.getServerData(), ParticleEffectType.SMOKE, this, worldCoordinates.x+1.4f, worldCoordinates.y-.8f, 2f, 1);
                }
            }

            if(steamCount > 0) {
                if(getPowerGrid()!=null) {
                    steamDelta += Tile.DELTA_TIME*getPowerGrid().getGenerationEfficiency();
                    if(steamDelta > STEAM_TIME) {
                        steamDelta -= STEAM_TIME;
                        steamCount--;
                        data.getServerData().onTileChange(worldCoordinates.x, worldCoordinates.y, 1);
                    }
                }
            }

            boolean machineCurrentlyProducing = isMachineProducing();
            // If the machine has changed state from WORKING <-> NOT WORKING, update the visual of the tile on the client side
            if(machineCurrentlyProducing != lastDisplayMachineProducing) {
                // Sets the value of the lastDisplayMachineFunctioning variable to the current state of the machine so it can be checked later
                lastDisplayMachineProducing = machineCurrentlyProducing;
                data.getServerData().onTileChange(worldCoordinates.x, worldCoordinates.y, 1);
            }
        }
    }

    public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE*2, TILE_SIZE*(3/5f));
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
    public void onBreak(World world, Player player, int x, int y) {
        TileUtils.dropItem(world, x, y, Items.getItem(ItemType.STEAM_ENGINE, 1));
        removeEnergyDistributor();
    }

    @Override
    protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
        if(clickType == 2) {
            if(player.getPlayer().getHeldItem() instanceof CoalItem) {
                if(steamCount < MAX_STEAM) {
                    steamCount++;
                    player.getPlayer().getInventory().remove(Items.getItem(ItemType.STEAM_CANISTER, 1));
                    player.inventoryChanged(true);
                    world.playSound("dirt", .8f, x, y);
                    TileUtils.spawnParticleOffset(world, ParticleEffectType.SMOKE, this, x+1.4f, y-.8f, 2, 3);
                    world.onTileChange(x, y, 1);
                    return true;
                }
            }
        }
        return false;
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

    @Override
    public boolean canAcceptItem(Item item) {
        if(item instanceof SteamCanister) {
            return steamCount < MAX_STEAM;
        }
        return false;
    }

    @Override
    public void acceptItem(Item item) {
        steamCount++;
    }
}

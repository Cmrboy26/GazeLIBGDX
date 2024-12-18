package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.custom.CoalItem;
import net.cmr.gaze.inventory.custom.TorchItem;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.LightSource;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.MultiTile;
import net.cmr.gaze.world.abstractTiles.ElectricityPole;
import net.cmr.gaze.world.entities.Particle.ParticleEffectType;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;
import net.cmr.gaze.world.powerGrid.EnergyProducer;

public class CoalGenerator extends MultiTile implements EnergyProducer, LightSource {
    
    EnergyDistributor distributor;
    Point distributorPoint, worldCoordinates;
    boolean lastDisplayMachineProducing = false;
    float itemDelta;
    int coalCount;
    final int MAX_COAL = 20;
    final float COAL_TIME = 30;

    public CoalGenerator() {
        super(TileType.COAL_GENERATOR, 2, 1);
        distributorPoint = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		game.batch.draw(game.getSprite("coalGenerator"+(!lastDisplayMachineProducing?"Off":"")), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE*2, TILE_SIZE*3);
		super.render(game, screen, x, y);
	}

    float particleDelta;

    @Override
    public void update(TileData data, Point worldCoordinates, boolean updatedByPlayer) {
        if(data.isServer()) {
            if(coalCount > 0) {
                if(getPowerGrid()!=null) {
                    itemDelta += Tile.DELTA_TIME*getPowerGrid().getGenerationEfficiency();
                    if(itemDelta > COAL_TIME) {
                        itemDelta -= COAL_TIME;
                        coalCount--;
                        data.getServerData().onTileChange(worldCoordinates.x, worldCoordinates.y, 1);
                    }
                }
            }

            if(isMachineProducing()) {
                particleDelta+=Tile.DELTA_TIME*Math.random()*2;
                if(particleDelta > 2) {
                    particleDelta-=1;
                    TileUtils.spawnParticleOffset(data.getServerData(), ParticleEffectType.SMOKE, this, worldCoordinates.x+1, worldCoordinates.y, 2.65f, 1f/(Math.min(1f, (2f*coalCount/(coalCount+5f)))));
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

    @Override
    public void overrideGenerateInitialize(int x, int y, double seed) {
        setWorldCoordinates(new Point(x, y));
    }

    @Override
    public void overrideOnPlace(World world, int tx, int ty, Player player) {
        setWorldCoordinates(new Point(tx, ty));
        connectToWorld(world, tx, ty);
    }

    public boolean equals(Object object) {
        return super.equals(this) && object == this;
    }

    @Override
    protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
        if(clickType == 2) {
            if(player.getPlayer().getHeldItem() instanceof CoalItem) {
                if(coalCount < MAX_COAL) {
                    coalCount++;
                    player.getPlayer().getInventory().remove(Items.getItem(ItemType.COAL, 1));
                    player.inventoryChanged(true);
                    world.playSound("dirt", .8f, x, y);
                    TileUtils.spawnParticleOffset(world, ParticleEffectType.SMOKE, this, x+new Random().nextFloat(), y-2, 2, 3);
                    world.onTileChange(x, y, 1);
                    return true;
                }
            }
        }
        return false;
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
    public double getEnergyProduced() {
        if(coalCount > 0) {
            return 2f;
        }
        return 0;
    }

    @Override
    public void onBreak(World world, Player player, int x, int y) {
        TileUtils.dropItem(world, x, y, Items.getItem(ItemType.COAL_GENERATOR, 1));
        if(coalCount > 1) {
            TileUtils.dropItem(world, x, y, Items.getItem(ItemType.COAL, coalCount-1));
        }
        removeEnergyDistributor();
    }

    @Override
    public Tile readTile(DataInputStream input, TileType type) throws IOException {
        CoalGenerator coalGenerator = new CoalGenerator();
        Tile.readBreakData(input, coalGenerator);
        coalGenerator.distributorPoint = new Point(input.readInt(), input.readInt());
        coalGenerator.worldCoordinates = new Point(input.readInt(), input.readInt());
        coalGenerator.coalCount = input.readInt();
        coalGenerator.itemDelta = input.readFloat();
        coalGenerator.lastDisplayMachineProducing = input.readBoolean();
        return coalGenerator;
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
        buffer.writeInt(coalCount);
        buffer.writeFloat(itemDelta);
        buffer.writeBoolean(lastDisplayMachineProducing);
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
	public String getHitNoise() {
		return "stoneHit";
	}
	@Override
	public String getBreakNoise() {
		return "stoneBreak";
	}

    @Override
    public TileType[] belowWhitelist() {
        return null;
    }

    @Override
    public TileType[] belowBlacklist() {
        return defaultBlacklist;
    }
	
	public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE*2, TILE_SIZE*(4/5f));
    }

	@Override
	public float getIntensity() {
		return (6f+TorchItem.getTorchPulse(this))*(Math.min(1f, (2f*coalCount/(coalCount+5f))));
	}
    
    @Override
    public float offsetX() {
        return .5f;
    }

	@Override
	public Color getColor() {
		return TorchTile.TORCH_COLOR;
	}

}

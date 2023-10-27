package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.BaseTile;
import net.cmr.gaze.world.ElectricityPole;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.powerGrid.EnergyConsumer;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;

public class BlastFurnace extends BaseTile implements EnergyConsumer {

    EnergyDistributor distributor;
    Point distributorPoint;

    public BlastFurnace() {
        super(TileType.BLAST_FURNACE, 2, 1);
        distributorPoint = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void onPlace(World world, int tx, int ty, Player player) {
        // Search all neighbors within the radius of the pole
        // NOTE: this method does NOT need to be called on world load, as ElectricityPoles will 
        // connect every EnergyUser to the network on their own on server load
        ElectricityPole closestPole = null;
        float closestDistance = Float.MAX_VALUE;
        for(int x = tx - EnergyDistributor.MAX_RADIUS; x <= tx + EnergyDistributor.MAX_RADIUS; x++) {
            for(int y = ty - EnergyDistributor.MAX_RADIUS; y <= ty + EnergyDistributor.MAX_RADIUS; y++) {
                Tile tile = world.getTile(x, y, 1);
                if(tile instanceof ElectricityPole) {
                    float distance = (float) Math.hypot(x, y);
                    if(distance < closestDistance) {
                        closestDistance = distance;
                        closestPole = (ElectricityPole) tile;
                    }
                }
            }
        }
        setEnergyDistributor(closestPole);
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
    public double getEnergyConsumption() {
        return 1f;
    }

    @Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		game.batch.draw(game.getSprite("blastFurnace"), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE*2, TILE_SIZE*2);
		super.render(game, screen, x, y);
	}

    @Override
    public void update(TileData data, Point worldCoordinates) {
        if(data.isServer()) {
            System.out.println("POWER GRID POWER: "+getPowerGrid().getNetPower());
        }
    }

    @Override
    public void onBreak(World world, Player player, int x, int y) {
        TileUtils.dropItem(world, x, y, Items.getItem(ItemType.BLAST_FURNACE, 1));
        removeEnergyDistributor();
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

    @Override
    public Tile readTile(DataInputStream input, TileType type) throws IOException {
        BlastFurnace furnace = new BlastFurnace();
        Tile.readBreakData(input, furnace);
        furnace.distributorPoint = new Point(input.readInt(), input.readInt());
        return furnace;
    }

    @Override
    protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
        if(distributorPoint == null) {
            distributorPoint = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
        buffer.writeInt(distributorPoint.x);
        buffer.writeInt(distributorPoint.y);
    }
	
	public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE*2, TILE_SIZE*(4/5f));
    }
    
}

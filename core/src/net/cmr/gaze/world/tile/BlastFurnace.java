package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.crafting.Crafting.CraftingStation;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.custom.TorchItem;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.BaseTile;
import net.cmr.gaze.world.CraftingStationTile;
import net.cmr.gaze.world.LightSource;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.ElectricityPole;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.powerGrid.EnergyConsumer;
import net.cmr.gaze.world.powerGrid.EnergyDistributor;

public class BlastFurnace extends BaseTile implements EnergyConsumer, CraftingStationTile, LightSource {

    EnergyDistributor distributor;
    Point distributorPoint, worldCoordinates;

    public BlastFurnace() {
        super(TileType.BLAST_FURNACE, 2, 1);
        distributorPoint = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		game.batch.draw(game.getSprite("blastFurnace"), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE*2, TILE_SIZE*2);
		super.render(game, screen, x, y);
	}

    //

    @Override
    public void update(TileData data, Point worldCoordinates) {
        if(data.isServer()) {
            if(getPowerGrid()!=null) {
                //System.out.println("POWER GRID POWER: "+getPowerGrid().getMachineEfficiency()+" | "+getPowerGrid().getGenerationEfficiency());
            }
        }
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
    protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
        if(clickType == 2 && getPowerGrid() != null && getPowerGrid().getMachineEfficiency() > MIN_EFFICIENCY) {
			player.setCraftingStation(this, x, y);
			return true;
		}
        return false;
    }

    public boolean equals(Object object) {
        return super.equals(this) && object == this;
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
    public void onBreak(World world, Player player, int x, int y) {
        TileUtils.dropItem(world, x, y, Items.getItem(ItemType.BLAST_FURNACE, 1));
        removeEnergyDistributor();
    }

    @Override
    public Tile readTile(DataInputStream input, TileType type) throws IOException {
        BlastFurnace furnace = new BlastFurnace();
        Tile.readBreakData(input, furnace);
        furnace.distributorPoint = new Point(input.readInt(), input.readInt());
        furnace.worldCoordinates = new Point(input.readInt(), input.readInt());
        return furnace;
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
    }
    @Override
    public Point getWorldCoordinates() {
        return worldCoordinates;
    }

    @Override
    public void setWorldCoordinates(Point point) {
        this.worldCoordinates = point;
    }

    // 
    
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
    public CraftingStation getStation() {
        return CraftingStation.BLAST_FURNACE;
    }

	@Override
	public float getIntensity() {
		return 6+TorchItem.getTorchPulse(this);
	}
	@Override
	public Color getColor() {
		return TorchTile.TORCH_COLOR;
	}
    
}

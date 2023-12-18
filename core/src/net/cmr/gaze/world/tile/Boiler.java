package net.cmr.gaze.world.tile;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.custom.CoalItem;
import net.cmr.gaze.inventory.custom.SteamCanister;
import net.cmr.gaze.inventory.custom.WaterCanister;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.Tiles;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.ConveyorTile;
import net.cmr.gaze.world.abstractTiles.MultiTile;
import net.cmr.gaze.world.entities.Particle.ParticleEffectType;
import net.cmr.gaze.world.interfaceTiles.ConveyorDepositer;
import net.cmr.gaze.world.interfaceTiles.ConveyorReciever;
import net.cmr.gaze.world.interfaceTiles.Rotatable;

public class Boiler extends MultiTile implements ConveyorDepositer, ConveyorReciever{

    int coalAmount = 0;
    int waterAmount = 0;
    int steamAmount = 0;
    float coalDelta = 0;
    float waterDelta = 0;

    final int MAX_COAL = 32;
    final int MAX_WATER = 32;
    final float COAL_PER_SEC = 1f/10f;
    final float WATER_PER_SEC = 1f/5f;

    public Boiler() {
        super(TileType.BOILER, 2, 1);
    }

    float renderDelta = 0;

    @Override
    public void render(Gaze game, GameScreen screen, int x, int y) {
        renderDelta += Gdx.graphics.getDeltaTime()* (coalDelta > 0 ? 1 : 0);
        game.batch.draw(game.getAnimation("boiler").getKeyFrame(renderDelta), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE*2, TILE_SIZE*1.5f);
    }

    @Override
    public void update(TileData data, Point worldCoordinates, boolean updatedByPlayer) {
        if(data.isServer()) {
            // if coal finished and there is still water, add a new coal to be burned
            if(coalDelta <= 0 && coalAmount > 0 && waterAmount > 0) {
                coalDelta = 1;
                coalAmount--;
            }
            // if a piece of coal is burning and there is water, continue boiling the water
            if((coalDelta > 0 || coalAmount > 0) && waterAmount > 0) {
                waterDelta+=Tile.DELTA_TIME*WATER_PER_SEC;
            }
            // if a piece of coal is being burned, continue burning the coal
            if(coalDelta > 0) {
                coalDelta-=Tile.DELTA_TIME*COAL_PER_SEC;
            }
            // if water has finished boiling, destroy one canister, prepare the next water
            // canister, and export a steam canister
            if(waterDelta >= 1) {
                waterDelta -= 1;
                waterAmount--;
                steamAmount++;
            }
            // attempt to export the steam to the side conveyors
            if(steamAmount > 0) {
                Tile left = data.getTile(worldCoordinates.x-1, worldCoordinates.y, 1);
                Tile right = data.getTile(worldCoordinates.x+2, worldCoordinates.y, 1);
                if(left instanceof ConveyorReciever) {
                    ConveyorReciever conveyor = (ConveyorReciever) left;
                    if(left instanceof ConveyorTile) {
                        ConveyorTile conveyorTile = (ConveyorTile) left;
                        if(conveyorTile.getDirection() == Rotatable.Direction.LEFT) {
                            exportSteam(conveyorTile);
                        }
                    } else {
                        exportSteam(conveyor);
                    }
                } else if(right instanceof ConveyorReciever) {
                    ConveyorReciever conveyor = (ConveyorReciever) right;
                    if(right instanceof ConveyorTile) {
                        ConveyorTile conveyorTile = (ConveyorTile) right;
                        if(conveyorTile.getDirection() == Rotatable.Direction.RIGHT) {
                            exportSteam(conveyorTile);
                        }
                    } else {
                        exportSteam(conveyor);
                    }
                }
            }
        }
    }

    @Override
    protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
        if(clickType == 2) {
            if(player.getPlayer().getHeldItem() instanceof CoalItem) {
                if(coalAmount < MAX_COAL) {
                    coalAmount++;
                    player.getPlayer().getInventory().remove(Items.getItem(ItemType.COAL, 1));
                    player.inventoryChanged(true);
                    world.playSound("dirt", .8f, x, y);
                    TileUtils.spawnParticleOffset(world, ParticleEffectType.SMOKE, this, x+new Random().nextFloat(), y-2, 2, 3);
                    world.onTileChange(x, y, 1);
                    return true;
                }
            }
            else if(player.getPlayer().getHeldItem() instanceof WaterCanister) {
                if(waterAmount < MAX_WATER) {
                    waterAmount++;
                    player.getPlayer().getInventory().remove(Items.getItem(ItemType.WATER_CANISTER, 1));
                    player.inventoryChanged(true);
                    world.playSound("dirt", .8f, x, y);
                    //TileUtils.spawnParticleOffset(world, ParticleEffectType.SMOKE, this, x+new Random().nextFloat(), y-2, 2, 3);
                    world.onTileChange(x, y, 1);
                    return true;
                }
            }
        }
        return false;
    }

    private void exportSteam(ConveyorReciever conveyor) {
        Item steamCanister = Items.getItem(ItemType.STEAM_CANISTER, 1);
        depositItem(conveyor, steamCanister);
    }
    @Override
    public void onDepositItem(ConveyorReciever reciever, Item item) {
        steamAmount--;
    }

    @Override
    public boolean canAcceptItem(Item item) {
        if(item.getSize() != 1) return false;
        if(item instanceof CoalItem) {
            return coalAmount <= MAX_COAL;
        }
        if(item instanceof WaterCanister) {
            return waterAmount <= MAX_WATER;
        }
        return false;
    }

    @Override
    public void acceptItem(Item item) {
        if(item instanceof CoalItem) {
            coalAmount++;
        }
        if(item instanceof WaterCanister) {
            waterAmount++;
        }
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
        Boiler boiler = (Boiler) tile;
        boiler.coalAmount = input.readInt();
        boiler.waterAmount = input.readInt();
        boiler.steamAmount = input.readInt();
        boiler.coalDelta = input.readFloat();
        boiler.waterDelta = input.readFloat();
        return tile;
    }

    @Override
    protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
        buffer.writeInt(coalAmount);
        buffer.writeInt(waterAmount);
        buffer.writeInt(steamAmount);
        buffer.writeFloat(coalDelta);
        buffer.writeFloat(waterDelta);
    }
    
}

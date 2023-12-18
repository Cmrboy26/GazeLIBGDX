package net.cmr.gaze.world.tile;

import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileData;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.ConveyorTile;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.interfaceTiles.Rotatable;

public class BasicConveyorTile extends ConveyorTile {
    
    public BasicConveyorTile() {
        super(TileType.BASIC_CONVEYOR);
    }

    @Override
    public float getConveyorSpeed() {
        return 5f;
    }

    @Override
	public float getRenderYOffset() {
		return 1f;
	}

    @Override
    public void render(Gaze game, GameScreen screen, int x, int y) {
        draw(game.batch, game.getSprite("conveyor"+(this.getDirection()+1)), x, y, 1, 1);
        Item.draw(game, null, getItem(), game.batch, x*TILE_SIZE+TILE_SIZE*(1-1/1.5f)/2f, y*TILE_SIZE+TILE_SIZE*(1-1/1.5f)/2f, TILE_SIZE/1.5f, TILE_SIZE/1.5f);
        super.render(game, screen, x, y);
    }

    @Override
    public boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
        if(player.getPlayer().getHeldItem() != null) {
            if(getItem() != null) {
                return false;
            }
            Item item = player.getPlayer().getHeldItem().clone().set(1);
            acceptItem(item.clone());
            player.getPlayer().getInventory().remove(item, player.getPlayer().getHotbarSlot());
            player.inventoryChanged(true);
            world.onTileChange(x, y, 1);
            return true;
        } else {
            if(getItem() != null) {
                player.getPlayer().getInventory().add(getItem());
                player.inventoryChanged(true);
            }
            onDepositItem(null, getItem());
            world.onTileChange(x, y, 1);
            return true;
        }
    }
    
}

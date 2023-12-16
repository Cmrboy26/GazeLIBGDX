package net.cmr.gaze.world.tile;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.crafting.Crafting.CraftingStation;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.BaseTile;
import net.cmr.gaze.world.entities.Player;
import net.cmr.gaze.world.interfaceTiles.CraftingStationTile;

public class TechnologyTableTile extends BaseTile implements CraftingStationTile {

    public TechnologyTableTile() {
        super(TileType.TECHNOLOGY_TABLE, 2, 1);
    }

    @Override
    public CraftingStation getStation() {
        return CraftingStation.TECHNOLOGY_TABLE;
    }

	@Override
	public TileType[] belowWhitelist() {
		return null;
	}

    @Override
	public String getHitNoise() {
		return "stoneHit";
	}
	@Override
	public String getBreakNoise() {
		return "stoneHit";
	}

	@Override
	public TileType[] belowBlacklist() {
		return getDefaultBlacklist();
	}

	public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE*2, TILE_SIZE/2);
	}

    @Override
	public Tile readTile(DataInputStream input, TileType type) throws IOException {
		TechnologyTableTile table = new TechnologyTableTile();
		Tile.readBreakData(input, table);
		return table;
	}

    @Override
	protected boolean overrideOnInteract(PlayerConnection player, World world, int x, int y, int clickType) {
		if(clickType == 2) {
			player.setCraftingStation(this, x, y);
			return true;
		}
		return false;
	}

    @Override
    public void onBreak(World world, Player player, int x, int y) {
        TileUtils.dropItem(world, x, y, Items.getItem(ItemType.TECHNOLOGY_TABLE, 1));
    }

	@Override
	protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
		
	}

    @Override
    public void render(Gaze game, GameScreen screen, int x, int y) {
		draw(game.batch, game.getSprite("technologyTable"), x, y, 2, 1);
        super.render(game, screen, x, y);
    }
    
}

package net.cmr.gaze.world.tile;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class BedTile extends Tile {

    public BedTile() {
        super(TileType.BED);
    }

    @Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		game.batch.draw(game.getSprite("bed"), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE*1.5f);
		super.render(game, screen, x, y);
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
        BedTile tile = new BedTile();
        Tile.readBreakData(input, tile);
        return tile;
    }

    @Override
    protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
        
    }

    @Override
    public void onBreak(World world, Player player, int x, int y) {
        TileUtils.dropItem(world, x, y, Items.getItem(ItemType.BED, 1));
    }

    @Override
	public String getHitNoise() {
		return "woodHit";
	}
	@Override
	public String getBreakNoise() {
		return "woodHit";
	}

    public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }
    
}

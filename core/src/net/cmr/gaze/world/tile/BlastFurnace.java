package net.cmr.gaze.world.tile;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.BaseTile;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.TileUtils;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.entities.Player;

public class BlastFurnace extends BaseTile {

    public BlastFurnace() {
        super(TileType.BLAST_FURNACE, 2, 1);
    }

    @Override
	public void render(Gaze game, GameScreen screen, int x, int y) {
		game.batch.draw(game.getSprite("blastFurnace"), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE*2, TILE_SIZE*2);
		super.render(game, screen, x, y);
	}

    @Override
    public void onBreak(World world, Player player, int x, int y) {
        TileUtils.dropItem(world, x, y, Items.getItem(ItemType.BLAST_FURNACE, 1));
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
        return furnace;
    }

    @Override
    protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {

    }
	
	public Rectangle getBoundingBox(int x, int y) {
		return new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE*2, TILE_SIZE*(4/5f));
	}
    
}

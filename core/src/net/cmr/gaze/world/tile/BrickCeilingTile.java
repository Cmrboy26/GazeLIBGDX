package net.cmr.gaze.world.tile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.RenderRule;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.abstractTiles.CeilingTile;
import net.cmr.gaze.world.interfaceTiles.Rotatable;

public class BrickCeilingTile extends CeilingTile implements Rotatable {

    public BrickCeilingTile() {
        super(TileType.BRICK_CEILING);
    }

    @Override
    public TileType[] belowBlacklist() {
        return null;
    }
    @Override
    public TileType[] belowWhitelist() {
        return null;
    }

    @Override
    public void render(Gaze game, GameScreen screen, int x, int y) {
        if(!Objects.equals(screen.currentRenderRule, RenderRule.HOUSE_RULE)) {
		    draw(game.batch, game.getSprite("shingles"+(getDirection()+1)), x, y+.5f, 1, 3);
            super.render(game, screen, x, y);
        }
    }

    @Override
    public Tile readTile(DataInputStream input, TileType type) throws IOException {
        BrickCeilingTile wct = new BrickCeilingTile();
        Tile.readBreakData(input, wct);
        Rotatable.readRotatableData(input, wct);
        return wct;
    }

    @Override
    protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {
        writeRotatableData(buffer);
    }

    @Override
    public String getHitNoise() {
        return "stoneHit";
    }
    
    int direction;
	@Override
	public int getDirection() {
		return direction;
	}
	@Override
	public void setDirection(int v) {
		direction = v;
	}
    @Override
    public int maxDirection() {
        return 9;
    }

}

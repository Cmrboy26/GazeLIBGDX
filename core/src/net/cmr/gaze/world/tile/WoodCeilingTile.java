package net.cmr.gaze.world.tile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.CeilingTile;
import net.cmr.gaze.world.RenderRule;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;

public class WoodCeilingTile extends CeilingTile {

    public WoodCeilingTile() {
        super(TileType.WOOD_CEILING);
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
		    draw(game.batch, game.getSprite("woodCeiling"), x, y+.5f, 1, 3);
            super.render(game, screen, x, y);
        }
    }

    @Override
    public Tile readTile(DataInputStream input, TileType type) throws IOException {
        WoodCeilingTile wct = new WoodCeilingTile();
        Tile.readBreakData(input, wct);
        return wct;
    }

    @Override
    protected void writeTile(TileType tile, DataBuffer buffer) throws IOException {

    }

    @Override
    public String getHitNoise() {
        return "woodHit";
    }
    
}

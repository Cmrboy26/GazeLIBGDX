package net.cmr.gaze.world.abstractTiles;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;

public abstract class CeilingTile extends Tile {

    public CeilingTile(TileType tileType) {
        super(tileType);
    }

    @Override
    public TileType[] belowWhitelist() {
        return null;
    }

    @Override
    public TileType[] belowBlacklist() {
        return null;
    }

    public abstract Tile readTile(DataInputStream input, TileType type) throws IOException;
    protected abstract void writeTile(TileType tile, DataBuffer buffer) throws IOException;
    
}

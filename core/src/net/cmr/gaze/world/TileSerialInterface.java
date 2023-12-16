package net.cmr.gaze.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface TileSerialInterface {
    
    public void write(Tile tile, DataOutputStream output) throws IOException;
    public Tile read(DataInputStream input, TileType type) throws IOException;

}

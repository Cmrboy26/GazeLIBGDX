package net.cmr.gaze;

import net.cmr.gaze.world.Chunk;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.Tiles;

public class ConsoleTest {

    static Tile[][][] world;

    public static void main(String[] args) {
        Gaze.initializeGameContent();
        world = new Tile[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE][Chunk.LAYERS];
        for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                world[x][y][0] = Tiles.getTile(TileType.GRASS);
            }
        }
        world[8][8][1] = Tiles.getTile(TileType.ANVIL);

        
        printWorld();
    }

    public static void printWorld() {
        for(int y = 0; y < Chunk.CHUNK_SIZE; y++) {
            for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
                for(int z = Chunk.LAYERS-1; z >= 0; z--) {
                    Tile tile = world[x][y][z];
                    if(tile != null) {
                        System.out.print(tile.getType().name().charAt(0)+" ");
                        break;
                    }
                }
            }
            System.out.println();
        }
    }
}

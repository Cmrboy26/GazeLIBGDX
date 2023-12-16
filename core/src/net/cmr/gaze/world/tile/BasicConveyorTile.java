package net.cmr.gaze.world.tile;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.TileType;
import net.cmr.gaze.world.abstractTiles.ConveyorTile;
import net.cmr.gaze.world.interfaceTiles.Rotatable;

public class BasicConveyorTile extends ConveyorTile {
    
    public BasicConveyorTile() {
        super(TileType.BASIC_CONVEYOR);
    }

    @Override
    public float getConveyorSpeed() {
        return 1f;
    }

    @Override
    public void render(Gaze game, GameScreen screen, int x, int y) {
        draw(game.batch, game.animations.get("basicConveyor"+this.getDirection()), x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }
    
}

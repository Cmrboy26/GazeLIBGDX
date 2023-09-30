package net.cmr.gaze.world.pathfind;

import java.awt.Point;

import com.badlogic.gdx.math.Vector2;

import net.cmr.gaze.util.Vector2Double;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.entities.Entity;

public class DirectWalk {
    public static Vector2 walkDirectlyTowards(Entity entity, Point targetTile) {
        if(targetTile==null) {
            return null;
        }

        double targetX = targetTile.x*Tile.TILE_SIZE+Tile.TILE_SIZE/2f;
        double targetY = targetTile.y*Tile.TILE_SIZE+Tile.TILE_SIZE/2f;

        double entityX = entity.getX();
        double entityY = entity.getY()-entity.getBoundingBox(entity, 0, 0).height/2f;

        double xDiff = targetX - entityX;
        double yDiff = targetY - entityY;

        double threshold = Tile.TILE_SIZE/10f;

        if(xDiff == 0 && yDiff == 0) {
            return null;
        }
        
        double x = Math.abs(xDiff)/xDiff;
        double y = Math.abs(yDiff)/yDiff;

        boolean allowX = false;
        allowX = true;
        if(Math.abs(yDiff) < threshold) {
            y = 0;
        }
        if(allowX) {
            if(Math.abs(xDiff) < threshold) {
                x = 0;
            }
        } else {
            x = 0;
        }

        return new Vector2((float)x, (float)y);
    }
}

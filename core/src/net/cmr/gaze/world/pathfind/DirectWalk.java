package net.cmr.gaze.world.pathfind;

import java.awt.Point;

import com.badlogic.gdx.math.Vector2;

import net.cmr.gaze.world.entities.Entity;

public class DirectWalk {
    public static Vector2 walkDirectlyTowards(Entity entity, Point targetTile) {
        if(targetTile==null) {
            return null;
        }
        Vector2 vector = new Vector2(targetTile.x - entity.getPathTileX(), targetTile.y - entity.getPathTileY());
        if(vector.x != 0) {
            vector.x = vector.x / Math.abs(vector.x);
        }
        if(vector.y != 0) {
            vector.y = vector.y / Math.abs(vector.y);
        }
        return vector;
    }
}

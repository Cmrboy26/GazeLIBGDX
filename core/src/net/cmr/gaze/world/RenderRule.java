package net.cmr.gaze.world;

import java.util.Objects;

import com.badlogic.gdx.Gdx;

import net.cmr.gaze.world.abstractTiles.CeilingTile;

public abstract class RenderRule {
    
    public static float delta = 0;
    static RenderRule lastRenderRule;

    public static final RenderRule DEFAULT_RULE = new RenderRule() {
        @Override
        public float isRenderableTile(Tile[][][] chunkData, int x, int y, int z) {
            Tile tile = chunkData[x][y][z];
            if(tile instanceof Housing || tile instanceof CeilingTile) {
                return 1f;
            } else {
                return 1f*(delta)+0.001f;
            }
        }
    };
    public static final RenderRule HOUSE_RULE = new RenderRule() {
        @Override
        public float isRenderableTile(Tile[][][] chunkData, int x, int y, int z) {
            Tile tile = chunkData[x][y][z];
            if(tile instanceof Housing || tile instanceof CeilingTile) {
                return 1f;
            } else {
                //return 1f*(1f-delta)+0.001f;
                return 1f;
            }
        }
    };

    public abstract float isRenderableTile(Tile[][][] chunkData, int x, int y, int z);

    /**
     * 
     * @param chunkData
     * @param x
     * @param y
     * @return the alpha of the determined tile
     */
    public float renderTile(Tile[][][] chunkData, int x, int y) {

        // OPTIONALLY UNCOMMENT, ADD SETTINGS FOR IT LATER
        delta = 1;

        if(!Objects.equals(lastRenderRule, this) || lastRenderRule == null) {
            delta = 0;
            lastRenderRule = this;
        }

        float alpha = 0;
        for(int z = 0; z < Chunk.LAYERS; z++) {
            if(chunkData[x][y][z] != null) {
                float tempalpha = isRenderableTile(chunkData, x, y, z);
                if(tempalpha == 0) {
                    return 0f;
                }
                alpha = Math.max(alpha, tempalpha);
            }
        }
        return alpha;
    }


}

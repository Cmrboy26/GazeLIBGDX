package net.cmr.gaze.world;

public abstract class RenderRule {
    
    public static final RenderRule DEFAULT_RULE = new RenderRule() {
        @Override
        public boolean isRenderableTile(Tile tile) {
            return true;
        }
    };
    public static final RenderRule HOUSE_RULE = new RenderRule() {
        @Override
        public boolean isRenderableTile(Tile tile) {
            return tile instanceof Housing;
        }
    };

    public abstract boolean isRenderableTile(Tile tile);

    public boolean renderTile(Tile[][][] chunkData, int x, int y) {
        for(int z = 0; z < Chunk.LAYERS; z++) {
            if(chunkData[x][y][z] != null) {
                if(isRenderableTile(chunkData[x][y][z])) {
                    return true;
                }
            }
        }
        return false;
    }


}

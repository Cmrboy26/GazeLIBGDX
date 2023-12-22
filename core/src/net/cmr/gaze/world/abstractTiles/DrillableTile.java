package net.cmr.gaze.world.abstractTiles;

import net.cmr.gaze.world.interfaceTiles.ExploitableTile;

public interface DrillableTile extends ExploitableTile {

    public static final float DRILL_TIME_TIER_1 = 5f;
    public static final float DRILL_TIME_TIER_2 = 20f;
    public static final float DRILL_TIME_TIER_3 = 60f;

    /**
     * Time it takes to drill the tile in seconds.
     * @return time in seconds
     */
    public abstract float getDrillTime();

    @Override
    default ExploitType getExploitType() {
        return ExploitType.DRILL;
    }
    
}

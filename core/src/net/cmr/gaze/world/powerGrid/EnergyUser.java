package net.cmr.gaze.world.powerGrid;

import java.awt.Point;

import com.badlogic.gdx.utils.Null;

import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.World;
import net.cmr.gaze.world.abstractTiles.ElectricityPole;

public interface EnergyUser {
    
    /**
     * @return the {@link EnergyDistributor} this user is connected to. May be null
     */
    public @Null EnergyDistributor getEnergyDistributor();
    public void setEnergyDistributor(EnergyDistributor distributor);
    public void removeEnergyDistributor();
    public Point getWorldCoordinates();
    public void setWorldCoordinates(Point point);
    public default PowerGrid getPowerGrid() {
        EnergyDistributor distributor = getEnergyDistributor();
        if(distributor != null) {
            return distributor.getPowerGrid();
        }
        return null;
    }

    public default void connectToWorld(World world, int tx, int ty) {
        ElectricityPole closestPole = null;
        float closestDistance = Float.MAX_VALUE;
        for(int x = tx - EnergyDistributor.MAX_RADIUS; x <= tx + EnergyDistributor.MAX_RADIUS; x++) {
            for(int y = ty - EnergyDistributor.MAX_RADIUS; y <= ty + EnergyDistributor.MAX_RADIUS; y++) {
                Tile tile = world.getTile(x, y, 1);
                if(tile instanceof ElectricityPole) {
                    float distance = (float) Math.hypot(x-tx, y-ty);
                    int chebychev = Math.max(Math.abs(x-tx), Math.abs(y-ty));
                    if(distance < closestDistance && chebychev <= ((ElectricityPole) tile).getRadius()) {
                        closestDistance = distance;
                        closestPole = (ElectricityPole) tile;
                    }
                }
            }
        }
        setEnergyDistributor(closestPole);
        
    }

}

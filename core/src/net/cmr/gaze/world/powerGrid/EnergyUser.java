package net.cmr.gaze.world.powerGrid;

import com.badlogic.gdx.utils.Null;

public interface EnergyUser {
    
    /**
     * @return the {@link EnergyDistributor} this user is connected to. May be null
     */
    public @Null EnergyDistributor getEnergyDistributor();
    
}

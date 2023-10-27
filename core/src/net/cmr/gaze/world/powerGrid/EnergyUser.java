package net.cmr.gaze.world.powerGrid;

import com.badlogic.gdx.utils.Null;

public interface EnergyUser {
    
    /**
     * @return the {@link EnergyDistributor} this user is connected to. May be null
     */
    public @Null EnergyDistributor getEnergyDistributor();
    public void setEnergyDistributor(EnergyDistributor distributor);
    public void removeEnergyDistributor();
    public default PowerGrid getPowerGrid() {
        EnergyDistributor distributor = getEnergyDistributor();
        if(distributor != null) {
            return distributor.getPowerGrid();
        }
        return null;
    }


}

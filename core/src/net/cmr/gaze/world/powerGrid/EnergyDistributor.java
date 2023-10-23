package net.cmr.gaze.world.powerGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface EnergyDistributor {
    
    public void setPowerGrid(PowerGrid grid);
    public PowerGrid getPowerGrid();
    
    public ArrayList<EnergyDistributor> getNeighbors();
    public void clearNeighbors();
    public void addNeighbor(EnergyDistributor neighbor);
    public void removeNeighbor(EnergyDistributor neighbor);

}

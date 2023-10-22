package net.cmr.gaze.world.powerGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface EnergyDistributor {
    
    
    public void setPowerGrid(PowerGrid grid);
    public PowerGrid getPowerGrid();
    public int getPowerGridID();
    public void setPowerGridID(int id);

    boolean equals(Object obj);


    /*// should contain a list of EnergyElements
    public List<EnergyDistributor> getNeighbors();
    public void removeAllNeighbors();

    public List<EnergyConsumer> getEnergyConsumers();
    public List<EnergyProducer> getEnergyProducers();
    public PowerGrid getPowerGrid();
    public void setPowerGrid(PowerGrid grid);

    public default void remove() {

        PowerGrid original = getPowerGrid();
        ArrayList<PowerGrid> grid = new ArrayList<>();
        int index = 0;

        ArrayList<EnergyDistributor> neighbors = new ArrayList<>(getNeighbors());
        removeAllNeighbors();

        for(EnergyDistributor neighbor : neighbors) {
            if(grid.get(index)==null) grid.add(new PowerGrid());
            if(Objects.equals(neighbor.getPowerGrid(), original)) {
                setNeighborWebPowerGrid(grid.get(index), neighbor);
            }
            index++;
        }

    }

    /** 
     * Sets the power grid of the distributor and all of its neighbors to the given grid.
    
    public static void setNeighborWebPowerGrid(PowerGrid grid, EnergyDistributor distributor) {
        if(!Objects.equals(distributor.getPowerGrid(), grid)) {
            distributor.setPowerGrid(grid);
            for(EnergyDistributor neighbor : distributor.getNeighbors()) {
                setNeighborWebPowerGrid(grid, neighbor);
            }
        }
    }*/

}

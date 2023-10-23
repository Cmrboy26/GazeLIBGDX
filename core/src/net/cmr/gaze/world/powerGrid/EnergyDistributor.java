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

    public static class DefaultEnergyDistributor implements EnergyDistributor {
        PowerGrid grid;
        ArrayList<EnergyDistributor> neighbors = new ArrayList<>();
        @Override
        public void setPowerGrid(PowerGrid grid) {
            this.grid = grid;
        }

        @Override
        public PowerGrid getPowerGrid() {
            return grid;
        }

        @Override
        public ArrayList<EnergyDistributor> getNeighbors() {
            return neighbors;
        }

        @Override
        public void clearNeighbors() {
            neighbors.clear();
        }

        @Override
        public void addNeighbor(EnergyDistributor neighbor) {
            if(!neighbors.contains(neighbor)) {
                neighbors.add(neighbor);
            }
        }

        @Override
        public void removeNeighbor(EnergyDistributor neighbor) {
            neighbors.remove(neighbor);
        }

    }

}

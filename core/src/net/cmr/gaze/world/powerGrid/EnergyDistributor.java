package net.cmr.gaze.world.powerGrid;

import java.util.ArrayList;
import java.util.Objects;

public interface EnergyDistributor {
    
    public void setPowerGrid(PowerGrid grid);
    public PowerGrid getPowerGrid();
    
    public ArrayList<EnergyDistributor> getNeighbors();
    public void clearNeighbors();
    void addNeighbor(EnergyDistributor neighbor);
    public void removeNeighbor(EnergyDistributor neighbor);

    /**
    * WARNING!!!: USE {@link #connectNodes(EnergyDistributor, EnergyDistributor)} INSTEAD, this method is only for internal use.
    */
    public static void connectNodes(EnergyDistributor node1, EnergyDistributor node2) {
        node1.addNeighbor(node2);
        node2.addNeighbor(node1);
    }

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
            if(!Objects.equals(neighbor.getPowerGrid(), grid)) {
                neighbor.setPowerGrid(grid);
            }
        }

        @Override
        public void removeNeighbor(EnergyDistributor neighbor) {
            neighbors.remove(neighbor);
        }

    }

}

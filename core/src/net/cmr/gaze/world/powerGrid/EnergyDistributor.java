package net.cmr.gaze.world.powerGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface EnergyDistributor {
    
    public static final int MAX_RADIUS = 10;

    public EnergySubnet getEnergyUsers();
    public default void addEnergyUser(EnergyUser user) {
        getEnergyUsers().addUser(user);
    }
    public default void removeEnergyUser(EnergyUser user) {
        getEnergyUsers().removeUser(user);
    }

    public void setPowerGrid(PowerGrid grid);
    public PowerGrid getPowerGrid();
    
    public List<EnergyDistributor> getNeighbors();
    public void clearNeighbors();
    void addNeighbor(EnergyDistributor neighbor);
    public void removeNeighbor(EnergyDistributor neighbor);

    public default void removeFromGrid() {
        if(getPowerGrid() != null) {
            getPowerGrid().remove(this, true);
        }
    }

    public static void connectNodes(EnergyDistributor node1, EnergyDistributor node2) {
        Objects.requireNonNull(node1);
        Objects.requireNonNull(node2);
        if(node1.getNeighbors().contains(node2) || node2.getNeighbors().contains(node1)) {
            return;
        }

        node1.addNeighbor(node2);
        node2.addNeighbor(node1);
        //System.out.println("Connected nodes: "+node1+" and "+node2);
        PowerGrid.adaptiveSetGrid(node1);
        PowerGrid.adaptiveSetGrid(node2);
    }

    public static void disconnectNodes(EnergyDistributor node1, EnergyDistributor node2) {
        node1.removeNeighbor(node2);
        node2.removeNeighbor(node1);
        //System.out.println("Disconnected nodes: "+node1+" and "+node2);
    }

    public static class DefaultEnergyDistributor implements EnergyDistributor {
        PowerGrid grid;
        ArrayList<EnergyDistributor> neighbors = new ArrayList<>();
        EnergySubnet subnet = new EnergySubnet(this);

        public EnergySubnet getEnergyUsers() {
            return subnet;
        }

        @Override
        public void setPowerGrid(PowerGrid grid) {
            this.grid = grid;
        }

        @Override
        public PowerGrid getPowerGrid() {
            return grid;
        }

        @Override
        public List<EnergyDistributor> getNeighbors() {
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

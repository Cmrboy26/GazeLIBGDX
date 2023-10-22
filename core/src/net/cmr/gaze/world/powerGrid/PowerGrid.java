package net.cmr.gaze.world.powerGrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PowerGrid {

    private AdjacencyMap adjacencyMap;

    public PowerGrid() {
        adjacencyMap = new AdjacencyMap();
    }

    private class AdjacencyMap extends HashMap<EnergyDistributor, List<EnergyDistributor>> {
        
    }

    public void addTransmitter(EnergyDistributor distributor) {

        if(this.equals(distributor.getPowerGrid())) {
            return;
        }/*else if(distributor.getPowerGrid()!=null) {
            // might be bad??
            distributor.getPowerGrid().removeTransmitter(distributor);
        }*/

        distributor.setPowerGrid(this);
        adjacencyMap.put(distributor, new ArrayList<>());
        remapIDs();
    }

    public void addConnection(EnergyDistributor distributor, EnergyDistributor neighbor) {
        if(!adjacencyMap.containsKey(distributor)) {
            addTransmitter(distributor);
        }
        if(!adjacencyMap.containsKey(neighbor)) {
            addTransmitter(neighbor);
        }
        adjacencyMap.get(distributor).add(neighbor);
        adjacencyMap.get(neighbor).add(distributor);
    }

    public void removeTransmitter(EnergyDistributor distributor) {
        List<EnergyDistributor> neighbors = adjacencyMap.get(distributor);
        for(EnergyDistributor neighbor : neighbors) {
            adjacencyMap.get(neighbor).remove(distributor);
        }

        PowerGrid[] grids = new PowerGrid[neighbors.size()];
        int index = 0;

        for(EnergyDistributor neighbor : neighbors) {
            if(grids[index]==null) grids[index] = new PowerGrid();
            if(this.equals(neighbor.getPowerGrid())) {
                recursiveSetPowerGrid(grids[index], neighbor);
            }
            index++;
        }

        adjacencyMap.remove(distributor);
        remapIDs();
    }

    public static void recursiveSetPowerGrid(PowerGrid newGrid, EnergyDistributor distributor) {
        if(distributor.getPowerGrid().equals(newGrid)) {
            return;
        }
        ArrayList<EnergyDistributor> neighbors = new ArrayList<>(distributor.getPowerGrid().getNeighbors(distributor));
        distributor.getPowerGrid().removeTransmitter(distributor);
        newGrid.addTransmitter(distributor);
        for(EnergyDistributor neighbor : neighbors) {
            recursiveSetPowerGrid(newGrid, neighbor);
        }
    }

    public List<EnergyDistributor> getNeighbors(EnergyDistributor distributor) {
        return adjacencyMap.get(distributor);
    }

    public void remapIDs() {
        int i = 0;
        for(EnergyDistributor distributor : adjacencyMap.keySet()) {
            distributor.setPowerGridID(i);
            i++;
        }
    }

    public String printGrid() {
        String s = "";
        for(EnergyDistributor distributor : adjacencyMap.keySet()) {
            s += distributor.getPowerGridID() + ": ";
            for(EnergyDistributor neighbor : adjacencyMap.get(distributor)) {
                s += neighbor.getPowerGridID() + ", ";
            }
            s += "\n";
        }
        return s;
    }

    /*ArrayList<EnergyDistributor> distributors;

    public PowerGrid() {
        distributors = new ArrayList<>();
    }

    public void addDistributor(EnergyDistributor dist, List<EnergyDistributor> neighbors) {
        distributors.add(dist);
        dist.setPowerGrid(this);
        for(EnergyDistributor neighbor : neighbors) {
            if(!distributors.contains(neighbor)) {
                distributors.add(neighbor);
                neighbor.setPowerGrid(this);
            }
        }
    }*/

}

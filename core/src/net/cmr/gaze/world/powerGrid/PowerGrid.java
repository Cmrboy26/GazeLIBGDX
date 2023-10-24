package net.cmr.gaze.world.powerGrid;

import java.util.ArrayList;
import java.util.Objects;

public class PowerGrid {

    ArrayList<EnergyDistributor> energyDistributors;

    public PowerGrid() {
        this.energyDistributors = new ArrayList<EnergyDistributor>();
    }

    public void addEnergyDistributor(EnergyDistributor distributor) {
        energyDistributors.add(distributor);
        distributor.setPowerGrid(this);
    }

    protected void removeEnergyDistributor(EnergyDistributor distributor) {
        energyDistributors.remove(distributor);
        distributor.setPowerGrid(null);
        ArrayList<EnergyDistributor> neighbors = snapBranches(distributor);
		for(int i = 0; i < neighbors.size(); i++) {
			EnergyDistributor neighbor = neighbors.get(i);
			if(Objects.equals(neighbor.getPowerGrid(), this)) {
				PowerGrid grid = new PowerGrid();
				setNetworkGrid(grid, neighbor);
				continue;
			}
		}
    }

	/**
	 * "Snaps" the "branches" of a specific energy distributor.
	 * In other words, it removes all the connections between the distributor and its neighbors.
	 * @param distributor
	 * @return the list of the distributor's neighbors prior to snaping
	 */
	public static ArrayList<EnergyDistributor> snapBranches(EnergyDistributor distributor) {
		ArrayList<EnergyDistributor> neighbors = new ArrayList<>();
		for(EnergyDistributor neighbor : distributor.getNeighbors()) {
			neighbors.add(neighbor);
			neighbor.removeNeighbor(distributor);
		}
		distributor.clearNeighbors();
		return neighbors;
	}

	public int getSize() {
		return energyDistributors.size();
	}

	public static void setNetworkGrid(PowerGrid grid, EnergyDistributor selectedDistributor) {
		if(Objects.equals(grid, selectedDistributor.getPowerGrid())) {
			return;
		}
		selectedDistributor.setPowerGrid(grid);
		for(EnergyDistributor neighbor : selectedDistributor.getNeighbors()) {
			setNetworkGrid(grid, neighbor);
		}
	}
}

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

	/**
	 * Effectively flood fills the network with the specified grid.
	 * 
	 * In other words, this method will recursively iterate through 
	 * every neighbor of selectedDistributor and set their grid to the
	 * specified grid.
	 * 
	 * @param grid the grid to set the network to
	 * @param selectedDistributor the distributor to start the fill from
	 */
	public static void setNetworkGrid(PowerGrid grid, EnergyDistributor selectedDistributor) {
		// If its already in the grid, return
		if(Objects.equals(grid, selectedDistributor.getPowerGrid())) {
			return;
		}

		// Removes the distributor from the old grid and adds it to the new grid
		if(selectedDistributor.getPowerGrid() != null) {
			selectedDistributor.getPowerGrid().removeEnergyDistributor(selectedDistributor);
		}
		grid.addEnergyDistributor(selectedDistributor);

		// Iterates through the rest of the neighbors
		for(EnergyDistributor neighbor : selectedDistributor.getNeighbors()) {
			setNetworkGrid(grid, neighbor);
		}
	}
}

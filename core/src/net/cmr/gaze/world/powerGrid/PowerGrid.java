package net.cmr.gaze.world.powerGrid;

import java.util.ArrayList;
import java.util.Objects;

public class PowerGrid {

    ArrayList<EnergyDistributor> energyDistributors;

    public PowerGrid() {
        this.energyDistributors = new ArrayList<EnergyDistributor>();
    }

	public void add(EnergyDistributor distributor) {
		if(distributor.getPowerGrid() != null) {
			distributor.getPowerGrid().removeEnergyDistributor(distributor);
		}
        energyDistributors.add(distributor);
        distributor.setPowerGrid(this);
	}

    void removeEnergyDistributor(EnergyDistributor distributor) {
        energyDistributors.remove(distributor);
        distributor.setPowerGrid(null);
        ArrayList<EnergyDistributor> neighbors = snapBranches(distributor);
		System.out.println(neighbors.size());
		for(int i = 0; i < neighbors.size(); i++) {
			EnergyDistributor neighbor = neighbors.get(i);

			if(Objects.equals(neighbor.getPowerGrid(), this)) {
				PowerGrid grid = new PowerGrid();
				grid.add(neighbor);
				setNetworkGrid(grid, neighbor, true);
				continue;
			}

			//if(Objects.equals(neighbor.getPowerGrid(), this)) {
				//setNetworkGrid(grid, neighbor, false);
				//continue;
			//}
		}
    }

	/**
	 * "Snaps" the "branches" of a specific energy distributor.
	 * In other words, it removes all the connections between the distributor and its neighbors.
	 * @param distributor
	 * @return the list of the distributor's neighbors prior to snaping
	 */
	public static ArrayList<EnergyDistributor> snapBranches(EnergyDistributor distributor) {
		ArrayList<EnergyDistributor> neighbors = new ArrayList<>(distributor.getNeighbors());
		for(EnergyDistributor neighbor : neighbors) {
			EnergyDistributor.disconnectNodes(neighbor, distributor);
		}
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
	public static void setNetworkGrid(PowerGrid grid, EnergyDistributor selectedDistributor, boolean first) {
		Objects.requireNonNull(grid);
		// If its already in the grid, return
		
		System.out.println("Setting "+selectedDistributor+" to22 "+grid);
		if(!first || !grid.equals(selectedDistributor.getPowerGrid())) { 
			if(Objects.equals(grid, selectedDistributor.getPowerGrid())) {
				return;
			}
			
			if(selectedDistributor.getPowerGrid() != null) {
				PowerGrid grid2 = selectedDistributor.getPowerGrid();
				grid2.energyDistributors.remove(selectedDistributor);
				selectedDistributor.setPowerGrid(null);
			}
			grid.add(selectedDistributor);
		}

		// Iterates through the rest of the neighbors
		ArrayList<EnergyDistributor> neighbors = new ArrayList<>(selectedDistributor.getNeighbors());
		for(EnergyDistributor neighbor : neighbors) {
			System.out.println("Setting "+neighbor+" to "+grid);
			setNetworkGrid(grid, neighbor, false);
		}
	}

	public static void adaptiveSetGrid(EnergyDistributor distributor) {

		// get the largest power grid of the neighbors
		// depth first search to set the grid of all its neighbors
		
		PowerGrid grid = distributor.getPowerGrid();
		if(grid == null) {
			grid = new PowerGrid();
		}
		for(EnergyDistributor neighbor : distributor.getNeighbors()) {
			if(neighbor.getPowerGrid() != null && neighbor.getPowerGrid().getSize() > grid.getSize()) {
				grid = neighbor.getPowerGrid();
			}
		}
		setNetworkGrid(grid, distributor, true);

	}
}

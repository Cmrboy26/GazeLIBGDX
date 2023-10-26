package net.cmr.gaze.world.powerGrid;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

public class PowerGrid {

    ArrayList<EnergyDistributor> energyDistributors;

    public PowerGrid() {
        this.energyDistributors = new ArrayList<EnergyDistributor>();
    }

	public void add(EnergyDistributor distributor) {
		if(distributor.getPowerGrid() != null) {
			distributor.getPowerGrid().removeEnergyDistributor(distributor);
		}
		addEnergyDistributor(distributor);
	}

	public void remove(EnergyDistributor distributor, boolean snap) {
        removeEnergyDistributor(distributor);
		if(snap) {
			ArrayList<EnergyDistributor> neighbors = snapBranches(distributor);
			System.out.println(neighbors.size());
			for(int i = 0; i < neighbors.size(); i++) {
				EnergyDistributor neighbor = neighbors.get(i);
				if(Objects.equals(neighbor.getPowerGrid(), this)) {
					PowerGrid grid = new PowerGrid();
					recursiveSetGrid(grid, neighbor);
					continue;
				}
			}
		}
	}

    void removeEnergyDistributor(EnergyDistributor distributor) {
        energyDistributors.remove(distributor);
        distributor.setPowerGrid(null);
    }

	void addEnergyDistributor(EnergyDistributor distributor) {
		energyDistributors.add(distributor);
		distributor.setPowerGrid(this);
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
	 * @param newGrid the grid to set the network to
	 * @param distributor the distributor to start the fill from
	 */
	public static void recursiveSetGrid(PowerGrid newGrid, EnergyDistributor distributor) {
		Objects.requireNonNull(newGrid);
		Objects.requireNonNull(distributor);
		
		Stack<EnergyDistributor> stack = new Stack<>();
		stack.push(distributor);
		
		while(!stack.isEmpty()) {
			EnergyDistributor current = stack.pop();
			if(!Objects.equals(current.getPowerGrid(), newGrid)) {
				newGrid.add(current);
				for(EnergyDistributor neighbor : current.getNeighbors()) {
					stack.push(neighbor);
				}
			}
		}
	}

	/**
	 *
	 * Adaptively ets the grid of the specified distributor to the largest grid of its neighbors.
	 * Utility method for {@link #recursiveSetGrid(PowerGrid, EnergyDistributor)}
	 * 
	 * @param distributor the distributor to set the grid of 
	 *
	*/
	public static void adaptiveSetGrid(EnergyDistributor distributor) {

		
		PowerGrid grid = distributor.getPowerGrid();
		if(grid == null) {
			grid = new PowerGrid();
		}
		for(EnergyDistributor neighbor : distributor.getNeighbors()) {
			if(neighbor.getPowerGrid() != null && neighbor.getPowerGrid().getSize() > grid.getSize()) {
				grid = neighbor.getPowerGrid();
			}
		}

		recursiveSetGrid(grid, distributor);

	}
}

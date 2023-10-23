package net.cmr.gaze.world.powerGrid;

import java.util.ArrayList;

public class PowerGrid {

    ArrayList<EnergyDistributor> energyDistributors;

    public PowerGrid() {
        this.energyDistributors = new ArrayList<EnergyDistributor>();
    }

    public void addEnergyDistributor(EnergyDistributor distributor) {
        energyDistributors.add(distributor);
        distributor.setPowerGrid(this);
    }

    public void removeEnergyDistributor(EnergyDistributor distributor) {
        energyDistributors.remove(distributor);
        distributor.setPowerGrid(null);
        ArrayList<EnergyDistributor> neighbors = new ArrayList<>(distributor.getNeighbors());
        for(EnergyDistributor neighbor : neighbors) {
            distributor.removeNeighbor(neighbor);
            neighbor.removeNeighbor(distributor);
        }
        for(int i = 0; i < neighbors.size(); i++) {
            EnergyDistributor neighbor = neighbors.get(i);
            ArrayList<EnergyDistributor> localGraph = new ArrayList<>();
            searchNode(neighbor, localGraph);
            if(localGraph.size() == energyDistributors.size()) {

            }
        }
    }
    /*
     * public void remove(GraphNode<T> splitPoint) {
		// iterate through neighbors of splitPoint
		// could maybe clone the whole map and remove connections once theyve been found so theres a big o of N, but bad memory 
		
		ArrayList<GraphNode<T>> removedConnections = new ArrayList<>();

		for(int i = 0; i < splitPoint.getConnections().size(); i++) {
			GraphNode<T> temp = splitPoint.getConnections().get(i);
			temp.removeConnection(splitPoint);
			removedConnections.add(temp);
		}
		splitPoint.setParentList(null);
		allConnections.remove(splitPoint);
		//System.out.println("REMOVING "+splitPoint.getData());
		for(int i = 0; i < removedConnections.size(); i++) {
			GraphNode<T> temp = removedConnections.get(i);
			ArrayList<GraphNode<T>> graph = new ArrayList<>();
			searchNode(temp, graph);
			//System.out.println(temp.getData()+" SEARCHED COUNT: "+graph.size());
			//System.out.println(i+": "+removedConnections.size()+","+graph.size()+", "+getSize());
			if(graph.size()!=getSize()) {
				// there is a split
				// loop through all the nodes in the split section
				// remove them from allConnections
				UndirectedGraph<T> newList = new UndirectedGraph<T>();
				graph.add(temp); // this may not work
				for(int v = 0; v < graph.size(); v++) {
					GraphNode<T> newNode = graph.get(v);
					if(newNode != splitPoint) {
						if(!newList.allConnections.contains(newNode)) {
							newNode.setParentList(newList);
							if(!allConnections.remove(newNode)) {
								// new ConcurrentModificationException();
							}
							newList.allConnections.add(newNode);
						}
					}
				}
				//System.out.println("NEW LIST: " +newList.getSize()+", "+newList.allConnections);
			}
		}

		splitPoint.clearConnections();
	}
     */

    public static void searchNode(EnergyDistributor connection, ArrayList<EnergyDistributor> progress) {
		for(int i = 0; i < connection.getNeighbors().size(); i++) {
			EnergyDistributor temp = connection.getNeighbors().get(i);
			if(!progress.contains(temp)) {
				progress.add(temp);
				searchNode(temp, progress);
			}
		}
	}
}

package net.cmr.gaze;

import java.io.InvalidObjectException;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.cmr.gaze.world.powerGrid.EnergyDistributor;
import net.cmr.gaze.world.powerGrid.PowerGrid;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) throws InvalidObjectException {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.setForegroundFPS(60);
		config.useVsync(false);
		config.setTitle("Gaze");
		config.setWindowedMode(640, 360);
		//new Lwjgl3Application(new Gaze() {}, config);

		PowerGrid grid = new PowerGrid();

		EnergyDistributor dist1 = new EnergyDistributor() {
			PowerGrid grid;
			int id;

			@Override
			public void setPowerGrid(PowerGrid grid) {
				this.grid = grid;
			}

			@Override
			public PowerGrid getPowerGrid() {
				return grid;
			}

			@Override
			public int getPowerGridID() {
				return id;
			}

			@Override
			public void setPowerGridID(int id) {
				this.id = id;
			}

			@Override
			public boolean equals(Object obj) {
				return obj instanceof EnergyDistributor && ((EnergyDistributor) obj).getPowerGridID() == id;
			}
		};
		EnergyDistributor dist2 = new EnergyDistributor() {
			PowerGrid grid;
			int id;

			@Override
			public void setPowerGrid(PowerGrid grid) {
				this.grid = grid;
			}

			@Override
			public PowerGrid getPowerGrid() {
				return grid;
			}

			@Override
			public int getPowerGridID() {
				return id;
			}

			@Override
			public void setPowerGridID(int id) {
				this.id = id;
			}

			@Override
			public boolean equals(Object obj) {
				return obj instanceof EnergyDistributor && ((EnergyDistributor) obj).getPowerGridID() == id;
			}
		};
		EnergyDistributor dist3 = new EnergyDistributor() {
			PowerGrid grid;
			int id;

			@Override
			public void setPowerGrid(PowerGrid grid) {
				this.grid = grid;
			}

			@Override
			public PowerGrid getPowerGrid() {
				return grid;
			}

			@Override
			public int getPowerGridID() {
				return id;
			}

			@Override
			public void setPowerGridID(int id) {
				this.id = id;
			}

			@Override
			public boolean equals(Object obj) {
				return obj instanceof EnergyDistributor && ((EnergyDistributor) obj).getPowerGridID() == id;
			}
		};
		EnergyDistributor dist4 = new EnergyDistributor() {
			PowerGrid grid;
			int id;

			@Override
			public void setPowerGrid(PowerGrid grid) {
				this.grid = grid;
			}

			@Override
			public PowerGrid getPowerGrid() {
				return grid;
			}

			@Override
			public int getPowerGridID() {
				return id;
			}

			@Override
			public void setPowerGridID(int id) {
				this.id = id;
			}

			@Override
			public boolean equals(Object obj) {
				return obj instanceof EnergyDistributor && ((EnergyDistributor) obj).getPowerGridID() == id;
			}
		};

		grid.addConnection(dist1, dist2);
		grid.addConnection(dist2, dist3);
		grid.addConnection(dist3, dist4);
		grid.addConnection(dist4, dist1);

		System.out.println(dist1.getPowerGridID()+" "+dist2.getPowerGridID()+" "+dist3.getPowerGridID()+" "+dist4.getPowerGridID());
		System.out.println(grid.printGrid());

		grid.removeTransmitter(dist2);
		
		System.out.println(dist1.getPowerGridID()+" "+dist3.getPowerGridID()+" "+dist4.getPowerGridID());
		System.out.println(grid.printGrid());

		grid.removeTransmitter(dist4);

		System.out.println(dist1.getPowerGridID()+" "+dist3.getPowerGridID());
		System.out.println(grid.printGrid());

		/*try {
			ResearchTree.deriveResearchGraph(
				"{\r\n" + //
						"\t\"comment\":\"the id will be appened to the namespace and all research nodes will append 'parent-id' to that\",\r\n" + //
						"\t\"namespace\":\"gaze\",\r\n" + //
						"\t\"root\":{\r\n" + //
						"\t\t\"name\":\"Machinery\",\r\n" + //
						"\t\t\"description\":\"The basis of all industry\",\r\n" + //
						"\t\t\"icon\":\"machinery\",\r\n" + //
						"\t\t\"id\":\"machinery\"\r\n" + //
						"\t},\r\n" + //
						"\t\"comment2\":\"the parent research is ALWAYS a requirement\",\r\n" + //
						"\t\"researchNodes\":[\r\n" + //
						"\t\t{\t\r\n" + //
						"\t\t\t\"name\":\"Gears\",\r\n" + //
						"\t\t\t\"description\":\"Gears and gadgets\",\r\n" + //
						"\t\t\t\"icon\":\"gearSymbol\",\r\n" + //
						"\t\t\t\"id\":\"gears\",\r\n" + //
						"\t\t\t\"parent-id\":\"root\",\r\n" + //
						"\t\t\t\"requirements\": [\r\n" + //
						"\t\t\t\t\"ITEM/IRON_INGOT/5\"\r\n" + //
						"\t\t\t],\r\n" + //
						"\t\t\t\"rewards\": []\r\n" + //
						"\t\t},\r\n" + //
						"\t\t{\r\n" + //
						"\t\t\t\"name\":\"Electricity\",\r\n" + //
						"\t\t\t\"description\":\"POWER\",\r\n" + //
						"\t\t\t\"icon\":\"electricSymbol\",\r\n" + //
						"\t\t\t\"id\":\"electricity\",\r\n" + //
						"\t\t\t\"parent-id\":\"gears\",\r\n" + //
						"\t\t\t\"requirements\":[\r\n" + //
						"\t\t\t\t\"ITEM/WOOD_PICKAXE/1\",\r\n" + //
						"\t\t\t\t\"RESEARCH/gaze:machinery.electricity\"\r\n" + //
						"\t\t\t],\r\n" + //
						"\t\t\t\"rewards\":[\r\n" + //
						"\t\t\t\t\"XP/MINING/10\",\r\n" + //
						"\t\t\t\t\"ITEM/WOOD_PICKAXE/1\"\r\n" + //
						"\t\t\t]\r\n" + //
						"\t\t},\r\n" + //
						"\t\t{\r\n" + //
						"\t\t\t\"name\":\"Power Transportation\",\r\n" + //
						"\t\t\t\"description\":\"Move around electricity\",\r\n" + //
						"\t\t\t\"icon\":\"powerPole\",\r\n" + //
						"\t\t\t\"id\":\"powerTransport\",\r\n" + //
						"\t\t\t\"parent-id\":\"electricity\",\r\n" + //
						"\t\t\t\"requirements\":[\r\n" + //
						"\t\t\t\t\"ITEM/COPPER_WIRE/10\"\r\n" + //
						"\t\t\t],\r\n" + //
						"\t\t\t\"rewards\":[\r\n" + //
						"\t\t\t\t\"XP/MINING/15\",\r\n" + //
						"\t\t\t\t\"ITEM/POWER_POLE/10\"\r\n" + //
						"\t\t\t]\r\n" + //
						"\t\t}\r\n" + //
						"\t]\r\n" + //
						"}");
		} catch (InvalidObjectException e) {
			e.printStackTrace();
		}*/
	}
}

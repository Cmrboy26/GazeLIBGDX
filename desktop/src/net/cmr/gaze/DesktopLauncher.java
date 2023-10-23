package net.cmr.gaze;

import java.io.InvalidObjectException;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.cmr.gaze.world.powerGrid.EnergyDistributor;
import net.cmr.gaze.world.powerGrid.EnergyDistributor.DefaultEnergyDistributor;
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

		EnergyDistributor[] distributors = new EnergyDistributor[9];
		for(int i = 0; i < distributors.length; i++) {
			distributors[i] = new DefaultEnergyDistributor();
			grid.addEnergyDistributor(distributors[i]);
		}

		EnergyDistributor.connectNodes(distributors[0], distributors[1]);
		EnergyDistributor.connectNodes(distributors[1], distributors[3]);
		EnergyDistributor.connectNodes(distributors[3], distributors[4]);
		EnergyDistributor.connectNodes(distributors[4], distributors[2]);
		EnergyDistributor.connectNodes(distributors[4], distributors[5]);
		EnergyDistributor.connectNodes(distributors[4], distributors[6]);
		EnergyDistributor.connectNodes(distributors[6], distributors[7]);
		EnergyDistributor.connectNodes(distributors[7], distributors[8]);

		grid.removeEnergyDistributor(distributors[4]);

		for(int i = 0; i < distributors.length; i++) {
			System.out.println(distributors[i].getPowerGrid());
		}

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

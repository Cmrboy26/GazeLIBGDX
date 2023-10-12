package net.cmr.gaze;

import java.io.InvalidObjectException;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.cmr.gaze.stage.widgets.TechMenu;
import net.cmr.gaze.stage.widgets.TechMenu.ResearchTree;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) throws InvalidObjectException {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.setForegroundFPS(60);
		config.useVsync(false);
		config.setTitle("Gaze");
		config.setWindowedMode(640, 360);
		//new Lwjgl3Application(new Gaze() {}, config);

		ResearchTree tree = ResearchTree.deriveResearchGraph("{\n" + //
				"\t\"comment\":\"the id will be appened to the namespace and all research nodes will append 'parent-id' to that\",\n" + //
				"\t\"namespace\":\"gaze\",\n" + //
				"\t\"root\":{\n" + //
				"\t\t\"name\":\"Machinery\",\n" + //
				"\t\t\"description\":\"The basis of all industry\",\n" + //
				"\t\t\"icon\":\"machinery\",\n" + //
				"\t\t\"id\":\"machinery\",\n" + //
				"\t\t\"position\": [0,0]\n" + //
				"\t},\n" + //
				"\t\"comment2\":\"the parent research is ALWAYS a requirement\",\n" + //
				"\t\"researchNodes\":[\n" + //
				"\t\t{\n" + //
				"\t\t\t\"name\":\"Gears\",\n" + //
				"\t\t\t\"description\":\"PTSD from Factorio (Ratios)\",\n" + //
				"\t\t\t\"icon\":\"gears\",\n" + //
				"\t\t\t\"id\":\"gears\",\n" + //
				"\t\t\t\"parent-id\":\"root\",\n" + //
				"\t\t\t\"position\": [0,10],\n" + //
				"\t\t\t\"requirements\":[\n" + //
				"\t\t\t\t\"SKILL/MINING/2\",\n" + //
				"\t\t\t\t\"ITEM/IRON_INGOT/5\"\n" + //
				"\t\t\t],\n" + //
				"\t\t\t\"rewards\": [\n" + //
				"\t\t\t\t\"XP/MINING/10\"\n" + //
				"\t\t\t]\n" + //
				"\t\t},\n" + //
				"\t\t{\n" + //
				"\t\t\t\"name\":\"Electricity\",\n" + //
				"\t\t\t\"description\":\"SHOCKING!\",\n" + //
				"\t\t\t\"icon\":\"electricity\",\n" + //
				"\t\t\t\"id\":\"electricity\",\n" + //
				"\t\t\t\"parent-id\":\"gears\",\n" + //
				"\t\t\t\"position\": [0,20],\n" + //
				"\t\t\t\"requirements\":[\n" + //
				"\t\t\t\t\"ITEM/COPPER_INGOT/5\",\n" + //
				"\t\t\t\t\"ITEM/IRON_GEAR/5\"\n" + //
				"\t\t\t],\n" + //
				"\t\t\t\"rewards\": [\n" + //
				"\t\t\t\t\"XP/MINING/10\"\n" + //
				"\t\t\t]\n" + //
				"\t\t},\n" + //
				"\t\t{\n" + //
				"\t\t\t\"name\":\"Lighting\",\n" + //
				"\t\t\t\"description\":\"Let there be light!\",\n" + //
				"\t\t\t\"icon\":\"lighting\",\n" + //
				"\t\t\t\"id\":\"lighting\",\n" + //
				"\t\t\t\"parent-id\":\"electricity\",\n" + //
				"\t\t\t\"position\": [10,30],\n" + //
				"\t\t\t\"requirements\":[\n" + //
				"\t\t\t\t\"ITEM/COPPER_WIRE/10\"\n" + //
				"\t\t\t],\n" + //
				"\t\t\t\"rewards\": [\n" + //
				"\t\t\t\t\"XP/MINING/10\"\n" + //
				"\t\t\t]\n" + //
				"\t\t},\n" + //
				"\t\t{\n" + //
				"\t\t\t\"name\":\"Circuitry\",\n" + //
				"\t\t\t\"description\":\"Circuits are awesome!\",\n" + //
				"\t\t\t\"icon\":\"circuitry\",\n" + //
				"\t\t\t\"id\":\"circuitry\",\n" + //
				"\t\t\t\"parent-id\":\"electricity\",\n" + //
				"\t\t\t\"position\": [0,30],\n" + //
				"\t\t\t\"requirements\":[\n" + //
				"\t\t\t\t\"ITEM/COPPER_WIRE/10\"\n" + //
				"\t\t\t],\n" + //
				"\t\t\t\"rewards\": [\n" + //
				"\t\t\t\t\"XP/MINING/10\"\n" + //
				"\t\t\t]\n" + //
				"\t\t},\n" + //
				"\t\t{\n" + //
				"\t\t\t\"name\":\"Steam Power\",\n" + //
				"\t\t\t\"description\":\"Steam is the future!\",\n" + //
				"\t\t\t\"icon\":\"steam\",\n" + //
				"\t\t\t\"id\":\"steam\",\n" + //
				"\t\t\t\"parent-id\":\"electricity\",\n" + //
				"\t\t\t\"position\": [-10,30],\n" + //
				"\t\t\t\"requirements\":[\n" + //
				"\t\t\t\t\"ITEM/COPPER_COIL/10\",\n" + //
				"\t\t\t\t\"ITEM/IRON_GEAR/10\"\n" + //
				"\t\t\t],\n" + //
				"\t\t\t\"rewards\": [\n" + //
				"\t\t\t\t\"XP/MINING/10\"\n" + //
				"\t\t\t]\n" + //
				"\t\t}\n" + //
				"\t]\n" + //
				"}");

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

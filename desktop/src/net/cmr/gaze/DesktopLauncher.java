package net.cmr.gaze;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.cmr.gaze.quests.QuestCategory;
import net.cmr.gaze.quests.Quests;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.setForegroundFPS(60);
		config.useVsync(false);
		config.setTitle("Gaze");
		config.setWindowedMode(640, 360);
		//new Lwjgl3Application(new Gaze() {}, config);
		
		QuestCategory category = Quests.getQuestCategory(Quests.FARMING);
		System.out.println();
	}
}

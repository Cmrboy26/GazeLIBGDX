package net.cmr.gaze;

import java.util.List;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.cmr.gaze.world.pathfind.AStar;
import net.cmr.gaze.world.pathfind.AStarNode;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.setForegroundFPS(60);
		config.useVsync(false);
		config.setTitle("Gaze");
		config.setWindowedMode(640, 360);
		
		//config.setDecorated(false);
		//config.setWindowedMode(1920, 360*2);
		
		new Lwjgl3Application(new Gaze() {}, config);
	}
}

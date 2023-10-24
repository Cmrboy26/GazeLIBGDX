package net.cmr.gaze;

import java.io.InvalidObjectException;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
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
		new Lwjgl3Application(new Gaze() {}, config);
	}
}

package net.cmr.gaze;

import java.io.InvalidObjectException;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {

	public static void main (String[] arg) throws InvalidObjectException {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(false);
		config.setTitle("Gaze");
		config.setWindowedMode(640, 360);
		config.setWindowIcon(FileType.Internal, "sprites/logo.png");
		new Lwjgl3Application(new Gaze() {}, config);
	}
}

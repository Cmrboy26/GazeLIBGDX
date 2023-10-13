package net.cmr.gaze;

import java.io.IOException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import net.cmr.gaze.networking.GameServer;
import net.cmr.gaze.networking.GameServer.ServerType;

public class ServerLauncher {

	public static void main(String[] args) {
		try {
			Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
			config.setInitialVisible(false);
			new Lwjgl3Application(new ApplicationAdapter() {
				@Override
				public void create() {
					Gdx.app.exit();
				}
			}, config);
			GameServer server = new GameServer(ServerType.DedicatedMultiplayer, "server");
			server.startServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

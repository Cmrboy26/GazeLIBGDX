package net.cmr.gaze;

import java.io.IOException;

import net.cmr.gaze.networking.GameServer;
import net.cmr.gaze.networking.GameServer.ServerType;

public class ServerLauncher {

	public static void main(String[] args) {
		try {
			GameServer server = new GameServer(ServerType.DedicatedMultiplayer, "server");
			server.startServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

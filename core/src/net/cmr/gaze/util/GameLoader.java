package net.cmr.gaze.util;

import com.badlogic.gdx.Gdx;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.networking.GameServer;
import net.cmr.gaze.networking.GameServer.ServerType;
import net.cmr.gaze.stage.ConnectingScreen;
import net.cmr.gaze.stage.MessageScreen;

public class GameLoader {

	public static void startSingleplayer(Gaze game, String worldName) {
		GameServer server = null;
		try {
			server = new GameServer(ServerType.SingleplayerPrivate, worldName);
		} catch(Exception e) {
			e.printStackTrace();
			game.setScreen(new MessageScreen(game, e.getMessage()));
			return;
		}
		game.setScreen(new ConnectingScreen(game, "localhost", server.getPort(), Gdx.app.getPreferences("LoginData").getString("username"), server));
	}
	
	public static void startMultiplayer(Gaze game) {
		GameServer server = null;
		try {
			server = new GameServer(ServerType.MultiplayerPublic, "hostedSave");
		} catch(Exception e) {
			e.printStackTrace();
			game.setScreen(new MessageScreen(game, e.getMessage()));
			return;
		}
		game.setScreen(new ConnectingScreen(game, "localhost", server.getPort(), Gdx.app.getPreferences("LoginData").getString("username"), server));
	}
	
	public static void joinMultiplayer(Gaze game, String ip, int port) {
		if(port == 0) {
			port = GameServer.DEFAULT_PORT;
		}
		game.setScreen(new ConnectingScreen(game, ip, port, Gdx.app.getPreferences("LoginData").getString("username")));
	}
	
}

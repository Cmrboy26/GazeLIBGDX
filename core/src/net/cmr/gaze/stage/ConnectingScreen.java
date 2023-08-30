package net.cmr.gaze.stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.networking.GameServer;
import net.cmr.gaze.networking.GameServer.ServerType;
import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketBuilder;
import net.cmr.gaze.networking.PacketSender;
import net.cmr.gaze.networking.packets.AuthenticationPacket;

public class ConnectingScreen implements Screen {

	final Gaze game;
	final String ip;
	final int port;
	String username;
	
	Socket socket;
	DataOutputStream dataOut;
	DataInputStream dataIn;
	GameServer server;
	
	PacketBuilder builder;
	PacketSender sender;
	
	public ConnectingScreen(final Gaze game, String ip, int port, String username, GameServer server) {
		this.game = game;
		this.ip = ip;
		this.port = port;
		this.username = username;
		this.server = server;
		if(server != null) {
			server.startServer();
		}
		join();
	}
	
	public ConnectingScreen(final Gaze game, String ip, int port, String username) {
		this.game = game;
		this.ip = ip;
		this.port = port;
		this.username = username;
		join();
	}
	
	public void join() {
		this.builder = new PacketBuilder(false, 1) {
			@Override
			public void processPacket(Packet packet) {
				processIncomingPacket(packet);
			}
		};
		this.sender = new PacketSender();
		
		try {
			socket = new Socket(ip, port);
			dataOut = new DataOutputStream(socket.getOutputStream());
			dataIn = new DataInputStream(socket.getInputStream());
			//sender.addPacket(new AuthenticationPacket(username, Gaze.version));
		} catch (IOException e) {
			//e.printStackTrace();
			//game.setScreen(new MessageScreen(game, e.getMessage()));
		}
	}
	
	private void processIncomingPacket(Packet packet) {
		if(packet instanceof AuthenticationPacket) {
			AuthenticationPacket incoming = (AuthenticationPacket) packet;
			UUID id = UUID.fromString(Gdx.app.getPreferences("LoginData").getString("credentials"));
			incoming.initialize(username, id, game.settings.getInteger("playerType"));
			sender.sendPacketInstant(dataOut, incoming);
			if(server == null) {
				game.setScreen(new GameScreen(game, username, socket, dataIn, dataOut, server, false));
			} else {
				game.setScreen(new GameScreen(game, username, socket, dataIn, dataOut, server, server.serverType==ServerType.SingleplayerPrivate));
			}
		}
	}
	
	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		if(socket == null) {
			game.setScreen(new MessageScreen(game, "Could not connect."));
			return;
		}
		
		if(socket != null && socket.isConnected()) {
			try {
				builder.build(dataIn);
			} catch (IOException e) {
				System.out.println("Error building: "+e.getMessage());
				e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
			sender.sendAll(dataOut);
		}
		
		game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
		game.batch.begin();
		
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		try {
			if(game.getScreen() instanceof GameScreen) {
				return;
			}
			if(socket == null) {
				return;
			}
			dataOut.close();
			dataIn.close();
			socket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	

}

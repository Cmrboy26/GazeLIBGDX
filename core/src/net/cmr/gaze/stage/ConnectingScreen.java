package net.cmr.gaze.stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.Logger;
import net.cmr.gaze.networking.GameServer;
import net.cmr.gaze.networking.GameServer.ServerType;
import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketBuilder;
import net.cmr.gaze.networking.PacketSender;
import net.cmr.gaze.networking.packets.AuthenticationPacket;
import net.cmr.gaze.util.Normalize;

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
	boolean joining = false;

	TextButton back;
	Stages stages;

	Thread connectionThread;
	final int TIMEOUT_TIME = 10;
	float connectionTime = 0;
	
	public ConnectingScreen(final Gaze game, String ip, int port, String username, GameServer server) {
		this.game = game;
		this.ip = ip;
		this.port = port;
		this.username = username;
		this.server = server;

		this.stages = new Stages(game);
		Gdx.input.setInputProcessor(stages.getInputMultiplexer());
		back = new TextButton("Back", game.getSkin(), "button");
		back.setPosition(20f, 30, Align.left);
		back.setWidth(200f);
		back.setHeight(50f);
		back.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("falseSelect", 1f);
		    	game.setScreen(new MainMenuScreen(game));
		    }
		});
		stages.get(Align.center).addActor(back);
	}
	
	public ConnectingScreen(final Gaze game, String ip, int port, String username) {
		this.game = game;
		this.ip = ip;
		this.port = port;
		this.username = username;

		this.stages = new Stages(game);
		Gdx.input.setInputProcessor(stages.getInputMultiplexer());
		back = new TextButton("Back", game.getSkin(), "button");
		back.setPosition(20f, 30, Align.left);
		back.setWidth(200f);
		back.setHeight(50f);
		back.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("falseSelect", 1f);
		    	game.setScreen(new MultiplayerSelectScreen(game));
		    }
		});
		stages.get(Align.center).addActor(back);
	}
	
	public void join() {
		this.builder = new PacketBuilder(false, 1) {
			@Override
			public void processPacket(Packet packet) {
				processIncomingPacket(packet);
			}
		};
		this.sender = new PacketSender();
		connectionThread = new Thread() {
			public void run() {
				try {
					Logger.log("INFO", "Attempting connection with IP: "+ip+", PORT: "+port+"...");
					socket = new Socket(ip, port);
					dataOut = new DataOutputStream(socket.getOutputStream());
					dataIn = new DataInputStream(socket.getInputStream());
				} catch (IOException e) {

				}
			}
		};
		connectionThread.start();
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
		connectionTime += delta;
		game.viewport.apply();
		game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
		game.batch.begin();
		String additionalPeriods = ".";
		for(int i = 0; i < (int)(connectionTime/1f); i++) {
			additionalPeriods += ".";
		}
		String displayString = "Connecting"+additionalPeriods;
		String displayString2 ="\nTime remaining: "+Normalize.truncateDouble(Math.abs((TIMEOUT_TIME-connectionTime)), 1)+"s";
		GlyphLayout layout = new GlyphLayout(game.getFont(20), displayString);
		GlyphLayout layout2 = new GlyphLayout(game.getFont(20), displayString2);
		game.getFont(20).draw(game.batch, displayString, (640-layout.width)/2f, 360/1.25f);
		game.getFont(20).draw(game.batch, displayString2, (640-layout2.width)/2f, 360/1.25f-20);
		game.batch.end();
		
		stages.act(delta);
		stages.render(game.batch, true);
		if(!joining) {
			if(server != null) {
				server.startServer();
			}
			join();
			joining = true;
		}
		if(socket == null && connectionTime > TIMEOUT_TIME) {
			game.setScreen(new MessageScreen(game, "Could not connect."));
			return;
		}
		
		if(socket != null && socket.isConnected()) {
			if(dataIn!=null) {
				try {
					builder.build(dataIn);
				} catch (IOException e) {
					System.out.println("Error building: "+e.getMessage());
					e.printStackTrace();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(dataOut!=null) {
				sender.sendAll(dataOut);
			}
		}
		
	}

	@Override
	public void resize(int width, int height) {
		stages.resize(width, height);
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
		stages.dispose();
		connectionThread.interrupt();
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

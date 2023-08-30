package net.cmr.gaze.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.networking.GameServer;

public class MultiplayerAddScreen implements Screen {

	final Gaze game;
	TextButton back, add;
	Viewport bottomViewport;
	Stage centerStage, bottomStage;
	InputMultiplexer multi;
	TextField serverName, serverIP, serverPort;
	
	public MultiplayerAddScreen(final Gaze game) {
		this.game = game;
		this.centerStage = new Stage();
		centerStage.setViewport(game.viewport);
		
		bottomViewport = new FitViewport(640, 360);
		bottomViewport.getCamera().position.set(320, 180, 0);
		bottomStage = new Stage();
		bottomStage.setViewport(bottomViewport);
		
		multi = new InputMultiplexer(centerStage, bottomStage);
		
		back = new TextButton("Back", game.getSkin(), "button");
		back.setPosition(20f, 30, Align.left);
		back.setWidth(200f);
		back.setHeight(50f);
		back.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
		    	game.setScreen(new MultiplayerSelectScreen(game));
		    }
		});
		bottomStage.addActor(back);
		
		add = new TextButton("Add", game.getSkin(), "button");
		add.setPosition(640-20-200, 30, Align.left);
		add.setWidth(200f);
		add.setHeight(50f);
		add.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
		    	String namee = serverName.getText();
		    	if(namee == null || namee.isEmpty()) {
		    		namee = serverName.getMessageText();
		    	}
		    	
		    	String ip = serverIP.getText();
		    	if(ip == null || ip.isEmpty()) {
		    		game.playSound("error", .5f);
		    		serverIP.setMessageText("No server IP is set!");
		    		return;
		    	}
		    	
		    	String portString = serverPort.getText();
		    	int port = GameServer.DEFAULT_PORT;
		    	if(portString != null && !portString.isEmpty()) {
		    		port = Integer.parseInt(portString);
		    	}
		    	
		    	MultiplayerSelectScreen screen = new MultiplayerSelectScreen(game);
		    	screen.new ServerContents(namee, ip, port).addToFile();
		    	screen.addServer(namee, ip, port);
		    	game.setScreen(screen);
		    }
		});
		bottomStage.addActor(add);
		
		
		serverName = new TextField("", game.getSkin(), "textFieldLarge");
		serverName.setBounds(320-41*3, 360-41-100, 82*3, 82);
		serverName.setAlignment(Align.center);
		serverName.setMaxLength(24);
		serverName.setMessageText("Server Name");
		serverName.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				
				if(c=='~'||c=='|') {
					return false;
				}
				if(Character.isSpaceChar(c)) {
					return true;
				}
				if(Character.isAlphabetic(c)) {
					return true;
				}
				if(Character.isDigit(c)) {
					return true;
				}
				
				return false;
			}
			
		});
		serverName.setSize(41*6, 41);
		centerStage.addActor(serverName);
		
		serverIP = new TextField("", game.getSkin(), "textFieldLarge");
		serverIP.setBounds(320-41*3, 360-41-100-50, 82*3, 82);
		serverIP.setAlignment(Align.center);
		serverIP.setMaxLength(32);
		serverIP.setMessageText("Server Address");
		serverIP.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				
				if(c=='~'||c=='|') {
					return false;
				}
				if(Character.isWhitespace(c)) {
					return false;
				}
				
				return true;
			}
			
		});
		serverIP.setSize(41*6, 41);
		centerStage.addActor(serverIP);
		
		serverPort = new TextField("", game.getSkin(), "textFieldLarge");
		serverPort.setBounds(320-41*3, 360-41-100-50-50, 82*3, 82);
		serverPort.setAlignment(Align.center);
		serverPort.setMaxLength(5);
		serverPort.setMessageText("Server Port");
		serverPort.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				
				if(Character.isDigit(c)) {
					return true;
				}
				
				return false;
			}
			
		});
		serverPort.setSize(41*6, 41);
		centerStage.addActor(serverPort);
		
		
		Gdx.input.setInputProcessor(multi);
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		game.viewport.apply();
		game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
		game.batch.begin();
		game.viewport.apply();
		String title = "Add Server";
		int size = 40;
		float xOffset = (-new GlyphLayout(game.getFont(size), title).width)/2;
		float yOffset = (-new GlyphLayout(game.getFont(size), title).height)/2;
		game.getFont(size).draw(game.batch, title, 640/2+xOffset, 360-30+yOffset);
		centerStage.act(delta);
		centerStage.draw();
		
		game.batch.setProjectionMatrix(bottomViewport.getCamera().combined);
		bottomViewport.apply();
		bottomStage.act(delta);
		bottomStage.draw();
		
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		centerStage.getViewport().update(width, height);
		bottomStage.getViewport().update(width, height);
		bottomViewport.setScreenY(0);
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
		centerStage.dispose();
		bottomStage.dispose();
	}

}

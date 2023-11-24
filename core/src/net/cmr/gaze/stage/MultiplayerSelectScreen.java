package net.cmr.gaze.stage;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.networking.GameServer;
import net.cmr.gaze.stage.widgets.Background;
import net.cmr.gaze.util.GameLoader;

public class MultiplayerSelectScreen implements Screen {

	final Gaze game;
	Stages stages;
	
	
	TextButton host, join, back;
	ImageButton addServer, removeServer;
	TextField usernameTextField;
	
	HashMap<TextButton, ServerContents> serverList;
	
	ScrollPane serverScroll;
	InputMultiplexer multiInput;
	
	public class ServerContents {
		
		String ip, name;
		int port;
		
		public ServerContents(String name, String ip, int port) {
			this.ip = ip;
			if(port == 0) {
				port = GameServer.DEFAULT_PORT;
			}
			this.port = port;
			this.name = name;
		}
		
		public String getIP() {
			return ip;
		}
		public int getPort() {
			return port;
		}
		public String getName() {
			return name;
		}
		public void addToFile() {
			FileHandle servers = Gdx.files.external("Gaze/servers.dat");
			if(!servers.exists() || servers.readString().isEmpty()) {
				servers.writeString("", true);
			}
			servers.writeString(getName()+"~"+getIP()+"~"+getPort()+"|", true);
		}
		public void removeFromFile() {
			FileHandle servers = Gdx.files.external("Gaze/servers.dat");
			/*if(!servers.exists() || servers.readString().isEmpty()) {
				servers.writeString("Local Server~localhost~"+GameServer.DEFAULT_PORT+"|", false);
			}*/
			if(!servers.exists() || servers.readString().isEmpty()) {
		 		servers.writeString("", true);
			}
			String input = servers.readString();
			String expected = getName()+"~"+getIP()+"~"+getPort()+"|";
			
			int index = input.indexOf(expected);
			if(index != -1) {
				String end = input.substring(0, index)+input.substring(index+expected.length());
				servers.writeString(end, false);
				game.setScreen(new MultiplayerSelectScreen(game));
			}
		}
		
	}
	
	public ArrayList<ServerContents> readSavedServers() {
		ArrayList<ServerContents> end = new ArrayList<>();
		FileHandle servers = Gdx.files.external("Gaze/servers.dat");
		/*if(!servers.exists() || servers.readString().isEmpty()) {
			servers.writeString("Local Server~localhost~"+GameServer.DEFAULT_PORT+"|", false);
		}*/
		 
		if(!servers.exists() || servers.readString().isEmpty()) {
			servers.writeString("", true);
		}
		
		String input = servers.readString();
		while (input.length() > 0) {

			String temp = input.substring(0, input.indexOf('|'));
			while (Character.isWhitespace(temp.charAt(0))) {
				temp = temp.substring(2);
			}

			int nameEnd = temp.indexOf('~');
			int IPend = temp.indexOf('~', nameEnd + 1);
			int portEnd = temp.length();

			String name = temp.substring(0, nameEnd);
			String IP = temp.substring(nameEnd + 1, IPend);
			String port = temp.substring(IPend + 1, portEnd);

			addServer(name, IP, Integer.parseInt(port));
			input = input.substring(input.indexOf('|') + 1, input.length());
		}
		return end;
	}
	
	public MultiplayerSelectScreen(final Gaze game) {
		this.game = game;
		serverList = new HashMap<>();
		this.stages = new Stages(game);
		
		multiInput = stages.getInputMultiplexer();
		
		host = new TextButton("Host", game.getSkin(), "buttonLarge");
		host.setBounds(320+75+5, 10, 150, 37.5f);
		host.align(Align.center);
		host.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("falseSelect", 1f);
				game.setScreen(new SaveSelectScreen(game, CreationType.Hosting));
		    }
		});
		
		stages.get(Align.bottom).addActor(host);
		join = new TextButton("Join", game.getSkin(), "buttonLarge");
		join.setBounds(320-75, 10, 150, 37.5f);
		join.align(Align.center);
		join.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("falseSelect", 1f);
				joinServer();
				//GameLoader.joinMultiplayer(game, "localhost", GameServer.DEFAULT_PORT);
		    }
		});
		
		stages.get(Align.bottom).addActor(join);
		
		back = new TextButton("Back", game.getSkin(), "buttonLarge");
		back.setBounds(320-150-75-5, 10, 150, 37.5f);
		back.align(Align.center);
		back.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("falseSelect", 1f);
		    	//Gdx.app.exit();
		    	game.setScreen(new MainMenuScreen(game));
		    }
		});
		
		stages.get(Align.bottom).addActor(back);
		
		ImageButtonStyle style = new ImageButtonStyle();
		style.over = new TextureRegionDrawable(game.getSprite("plusButtonSelected"));
		style.up = new TextureRegionDrawable(game.getSprite("plusButton"));
		style.checked = new TextureRegionDrawable(game.getSprite("plusButtonSelected"));
		
		//addServer = new ImageButton(new TextureRegionDrawable(game.getSprite("plusButton")), new TextureRegionDrawable(game.getSprite("plusButtonSelected")));
		addServer = new ImageButton(style);
		addServer.setBounds(320+150+75+10, 10f, 37.5f, 37.5f);
		addServer.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(removeServer.isChecked()) {
					removeServer.setChecked(false);
				}
				game.setScreen(new MultiplayerAddScreen(game));
				return false;
			}
		});
		
		stages.get(Align.bottom).addActor(addServer);
		
		ImageButtonStyle style2 = new ImageButtonStyle();
		style2.over = new TextureRegionDrawable(game.getSprite("minusButtonSelected"));
		style2.up = new TextureRegionDrawable(game.getSprite("minusButton"));
		style2.checked = new TextureRegionDrawable(game.getSprite("minusButtonSelected"));
		
		removeServer = new ImageButton(style2);
		removeServer.setBounds(320-150-75-10-37.5f, 10f, 37.5f, 37.5f);
		removeServer.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(addServer.isChecked()) {
					addServer.setChecked(false);
				}
				
				VerticalGroup table = (VerticalGroup) serverScroll.getChild(0);
				for(Actor t : table.getChildren()) {
					Actor act = ((Table)t).getChild(0);
					if(act instanceof TextButton) {
						TextButton b = (TextButton) act;
						b.setChecked(false);
					}
				}
				
				return false;
			}
		});
		
		stages.get(Align.bottom).addActor(removeServer);
		
		
		usernameTextField = new TextField(Gdx.app.getPreferences("LoginData").getString("username"), game.getSkin(), "textFieldLarge");
		usernameTextField.setPosition(320-41*3, 360-41-10);
		usernameTextField.setAlignment(Align.center);
		usernameTextField.setMaxLength(16);
		usernameTextField.setMessageText("Username");
		usernameTextField.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				if(Character.isAlphabetic(c)) {
					return true;
				}
				if(Character.isDigit(c)) {
					return true;
				}
				
				return false;
			}
			
		});
		usernameTextField.setSize(41*6, 41);
		usernameTextField.addListener(new InputListener() {
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				Preferences prefs = Gdx.app.getPreferences("LoginData");
				prefs.putString("username", usernameTextField.getText());
				prefs.flush();
				return super.keyTyped(event, character);
			}
		});
		stages.get(Align.top).addActor(usernameTextField);

		VerticalGroup table = new VerticalGroup();
		ScrollPaneStyle scrollStyle = new ScrollPaneStyle();
		serverScroll = new ScrollPane(table, scrollStyle);
		serverScroll.setScrollbarsVisible(true);
		serverScroll.setBounds(640/2-200, 360/2-100, 400, 200);

		table.space(5);
		TextButtonStyle stylee = new TextButtonStyle(game.getSkin().get("toggleLarge", TextButtonStyle.class));
		stylee.font = game.getFont(17);
		//for(int i = 0; i < 5; i++) {
		//	addServer(i+"AMOUGS", "localhost", 25561);
		//}
		

		
		readSavedServers();
		
		
		stages.get(Align.center).addActor(serverScroll);
		
		stages.get(Align.top).getRoot().addCaptureListener(new InputListener() {
		    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		        if (!(event.getTarget() instanceof TextField)) stages.get(Align.top).setKeyboardFocus(null);
		        return false;
		    }
		});
		Gdx.input.setInputProcessor(multiInput);
		
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		game.batch.setProjectionMatrix(game.backgroundViewport.getCamera().combined);
		game.batch.begin();
		Background.draw(game.batch, game.backgroundViewport);
		game.batch.end();
		
		stages.act(delta);
		stages.render(game.batch, true);
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
	}
	
	public void addServer(String servername, String IP, int port) {
		
		ServerContents contents = new ServerContents(servername, IP, port);
		
		VerticalGroup table = (VerticalGroup) serverScroll.getChild(0);
		TextButtonStyle stylee = new TextButtonStyle(game.getSkin().get("toggleLarge", TextButtonStyle.class));
		stylee.font = game.getFont(17);
		Table serverUnit = new Table();
		TextButton server = new TextButton(servername, stylee);
		//TextButton w = new TextButton(servername, stylee);
		serverUnit.add(server).width(240).height(40);
		//serverUnit.add(w).width(100).height(40);
		server.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				for(Actor t : table.getChildren()) {
					Actor act = ((Table)t).getChild(0);
					if(act instanceof TextButton) {
						TextButton b = (TextButton) act;
						if(b.isChecked() && b!=serverUnit) {
							b.setChecked(false);
						}
					}
				}
				
				if(removeServer.isChecked()) {
					table.removeActor(serverUnit);
					serverList.get(server).removeFromFile();
					serverList.remove(server);
					removeServer.setChecked(false);
				}
				
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		this.serverList.put(server, contents);
		table.addActor(serverUnit);
	}
	
	public void joinServer() {
		VerticalGroup table = (VerticalGroup) serverScroll.getChild(0);
		for(Actor t : table.getChildren()) {
			Actor act = ((Table)t).getChild(0);
			if(act instanceof TextButton) {
				TextButton b = (TextButton) act;
				if(b.isChecked()) {
					ServerContents contents = this.serverList.get(b);
					GameLoader.joinMultiplayer(game, contents.ip, contents.port);
					//System.out.println(contents.getIP()+":"+contents.getPort());
					//System.out.println(b.getText());
				}
			}
		}
	}

}

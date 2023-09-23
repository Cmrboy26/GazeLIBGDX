package net.cmr.gaze.stage;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
	Stage leftstage, centerstage, bottomstage, rightstage, topstage;
	
	
	TextButton host, join, back;
	ImageButton addServer, removeServer;
	TextField usernameTextField;
	
	HashMap<TextButton, ServerContents> serverList;
	
	ScrollPane serverScroll;
	
	Viewport leftViewport, centerViewport, rightViewport, bottomViewport, topViewport;
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
		leftstage = new Stage();
		centerstage = new Stage();
		rightstage = new Stage();
		bottomstage = new Stage();
		topstage = new Stage();
		
		multiInput = new InputMultiplexer(leftstage, centerstage, rightstage, bottomstage, topstage);
		
		leftViewport = new FitViewport(640, 360);
		leftViewport.getCamera().position.set(320, 180, 0);
		centerViewport = new FitViewport(640, 360);
		centerViewport.getCamera().position.set(320, 180, 0);
		rightViewport = new FitViewport(640, 360);
		rightViewport.getCamera().position.set(320, 180, 0);
		bottomViewport = new FitViewport(640, 360);
		bottomViewport.getCamera().position.set(320, 180, 0);
		topViewport = new FitViewport(640, 360);
		topViewport.getCamera().position.set(320, 180, 0);
		leftstage.setViewport(leftViewport);
		rightstage.setViewport(rightViewport);
		centerstage.setViewport(centerViewport);
		bottomstage.setViewport(bottomViewport);
		topstage.setViewport(topViewport);
		
		host = new TextButton("Host", game.getSkin(), "buttonLarge");
		host.setBounds(320+75+5, 10, 150, 37.5f);
		host.align(Align.center);
		host.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("falseSelect", 1f);
		    	//Gdx.app.exit();
		    	//game.setScreen(new MainMenuScreen(game));
		    	//GameLoader.startMultiplayer(game);
				game.setScreen(new SaveSelectScreen(game, CreationType.Hosting));
		    }
		});
		
		bottomstage.addActor(host);
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
		
		bottomstage.addActor(join);
		
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
		
		bottomstage.addActor(back);
		
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
		
		bottomstage.addActor(addServer);
		
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
		
		bottomstage.addActor(removeServer);
		
		
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
				if(usernameTextField.getTextFieldFilter().acceptChar(usernameTextField, character)) {
					Gdx.app.getPreferences("LoginData").putString("username", usernameTextField.getText());
					Gdx.app.getPreferences("LoginData").flush();
				}
				return super.keyTyped(event, character);
			}
		});
		topstage.addActor(usernameTextField);

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
		
		
		centerstage.addActor(serverScroll);
		
		topstage.getRoot().addCaptureListener(new InputListener() {
		    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		        if (!(event.getTarget() instanceof TextField)) topstage.setKeyboardFocus(null);
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
		
		game.batch.setProjectionMatrix(leftViewport.getCamera().combined);
		game.batch.begin();
		leftViewport.apply();
		leftstage.act();
		leftstage.draw();
		
		//leftViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//leftViewport.setScreenX(0);
		
		game.batch.end();
		
		game.batch.setProjectionMatrix(centerViewport.getCamera().combined);
		game.batch.begin();
		centerViewport.apply();
		//game.getFont(50).draw(game.batch, "Gaze", 30, 360-30);
		centerstage.act();
		centerstage.draw();
		game.batch.end();
		
		game.batch.setProjectionMatrix(rightViewport.getCamera().combined);
		game.batch.begin();
		rightViewport.apply();
		rightstage.act();
		rightstage.draw();
		game.batch.end();
		
		game.batch.setProjectionMatrix(bottomViewport.getCamera().combined);
		game.batch.begin();
		bottomViewport.apply();
		//game.getFont(50).draw(game.batch, "Gaze", 30, 360-30);
		bottomstage.act();
		bottomstage.draw();
		game.batch.end();
		
		game.batch.setProjectionMatrix(topViewport.getCamera().combined);
		game.batch.begin();
		topViewport.apply();
		//game.getFont(50).draw(game.batch, "Gaze", 30, 360-30);
		topstage.act();
		topstage.draw();
		game.batch.end();
		
	}

	@Override
	public void resize(int width, int height) {
		leftstage.getViewport().update(width, height, false);
		leftViewport.setScreenX(0);
		leftViewport.setScreenY(0);
		rightstage.getViewport().update(width, height);
		rightViewport.setScreenX(Gdx.graphics.getWidth()-rightViewport.getScreenWidth());
		rightViewport.setScreenY(0);
		centerstage.getViewport().update(width, height);
		bottomstage.getViewport().update(width, height);
		bottomViewport.setScreenY(0);
		topstage.getViewport().update(width, height);
		topViewport.setScreenY(Gdx.graphics.getHeight()-rightViewport.getScreenHeight());
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
		leftstage.dispose();
		centerstage.dispose();
		rightstage.dispose();
		bottomstage.dispose();
		topstage.dispose();
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

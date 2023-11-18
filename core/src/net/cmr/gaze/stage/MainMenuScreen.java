package net.cmr.gaze.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.widgets.Background;
import net.cmr.gaze.stage.widgets.HintMenu;

public class MainMenuScreen implements Screen {

	Gaze game; 
	Stages stages;

	
	final float topDistance = 140;
	final float spacing = 50;
	final float height = 40;
	TextButton play, playMultiplayer, settings, exit;
	TextField usernameTextField, otherThing;
	int mouseX, mouseY;
	
	public MainMenuScreen(final Gaze game) {
		this.game = game;
		Preferences prefs = SettingScreen.initializePreferences();
		this.stages = new Stages(game);
		
		play = new TextButton("Play", game.getSkin(), "button");
		play.setPosition(20f, 360f-topDistance, Align.left);
		play.setWidth(height*4);
		play.setHeight(height);
		play.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("trueSelect", 1f);
				game.setScreen(new SaveSelectScreen(game, CreationType.Singleplayer));
				//GameLoader.startSingleplayer(game);
		    }
		});
		stages.get(Align.left).addActor(play);
		playMultiplayer = new TextButton("Multiplayer", game.getSkin(), "button");
		playMultiplayer.setPosition(20f, 360f-topDistance-spacing, Align.left);
		playMultiplayer.setWidth(height*4);
		playMultiplayer.setHeight(height);
		playMultiplayer.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("trueSelect", 1f);
				game.setScreen(new MultiplayerSelectScreen(game));
				/*GameServer server = null;
				try {
					server = new GameServer(25565, false);
				} catch(IOException e) {
					
				}
				game.setScreen(new ConnectingScreen(game, "localhost", 25565, Gdx.app.getPreferences("LoginData").getString("username"), server));
				*/
		    }
		});
		stages.get(Align.left).addActor(playMultiplayer);
		settings = new TextButton("Settings", game.getSkin(), "button");
		settings.setPosition(20f, 360f-topDistance-spacing-spacing, Align.left);
		settings.setWidth(height*4);
		settings.setHeight(height);
		settings.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("select", 1f);
		    	game.setScreen(new SettingScreen(game));
		    }
		});
		stages.get(Align.left).addActor(settings);
		exit = new TextButton("Exit", game.getSkin(), "button");
		exit.setPosition(20f, 360f-topDistance-spacing-spacing-spacing, Align.left);
		exit.setWidth(height*4);
		exit.setHeight(height);
		exit.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
				game.playSound("falseSelect", 1f);
		    	Gdx.app.exit();
		    }
		});
		stages.get(Align.left).addActor(exit);
		
		//stage.addActor(new NavigationMenu(game));
		
		/*usernameTextField = new TextField(Gdx.app.getPreferences("LoginData").getString("username"), game.getSkin(), "textFieldLarge");
		usernameTextField.setPosition(320, 240+30);
		usernameTextField.setAlignment(Align.center);
		usernameTextField.setMaxLength(16);
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
		usernameTextField.setSize(300, 50);
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
		
		stage.addActor(usernameTextField);*/
		
		/*otherThing = new TextField("", game.getSkin(), "textFieldLarge");
		otherThing.setPosition(320, 240-30);
		otherThing.setAlignment(Align.center);
		otherThing.setSize(300, 50);
		stage.addActor(otherThing);*/
		
		Gdx.input.setInputProcessor(stages.getInputMultiplexer());
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
		
		stages.get(Align.topLeft).getViewport().apply(false);
		game.batch.setProjectionMatrix(stages.get(Align.topLeft).getCamera().combined);
		game.batch.begin();
		
		game.getFont(50).draw(game.batch, "Gaze", 30, 360-30);
		stages.act(delta);
		stages.render(game.batch, false);
		game.batch.end();
	}
	
	public void relativeVector(Vector2 vec, OrthographicCamera c){
		
		vec.set(vec.x+c.position.x-c.viewportWidth/2, vec.y+c.position.y-c.viewportHeight/2);
		
	   // return new Vector3(x+c.position.x-c.viewportWidth/2,y+c.position.y-c.viewportHeight/2,0);
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
		
	}

	@Override
	public void dispose() {
		stages.dispose();
		//backgroundStage.dispose();
	}

}

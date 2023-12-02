package net.cmr.gaze.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.widgets.Background;
import net.cmr.gaze.stage.widgets.HintMenu;
import net.cmr.gaze.stage.widgets.MainMenuWidget;
import net.cmr.gaze.stage.widgets.GameSettings;

public class MainMenuScreen implements Screen {

	Gaze game; 
	public Stages stages;
	MainMenuWidget mainMenuWidget;
	
	final float topDistance = 140;
	final float spacing = 50;
	final float height = 40;
	TextButton play, playMultiplayer, settings, exit;
	TextField usernameTextField, otherThing;
	int mouseX, mouseY;
	
	public MainMenuScreen(final Gaze game) {
		this.game = game;
		SettingScreen.initializePreferences();
		this.stages = new Stages(game);

		mainMenuWidget = new MainMenuWidget(game, this);
		mainMenuWidget.setPosition(10, 360/2f);
		stages.get(Align.left).addActor(mainMenuWidget);

		LabelStyle labelStyle = new LabelStyle(game.getFont(40), Color.WHITE);
		Label title = new Label("Gaze", labelStyle);
		title.setPosition(30, 360-30-50);
		stages.get(Align.topLeft).addActor(title);

		
		
		Gdx.input.setInputProcessor(stages.getInputMultiplexer());
	}
	
	@Override
	public void show() {
		
	}
	
	@Override
	public void render(float delta) {
		game.batch.setProjectionMatrix(game.backgroundViewport.getCamera().combined);
		game.batch.begin();
		Background.draw(game.batch, game.backgroundViewport);
		game.batch.end();
		
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
		mainMenuWidget.dispose();
		//backgroundStage.dispose();
	}

}

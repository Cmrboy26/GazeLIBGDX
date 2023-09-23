package net.cmr.gaze.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.widgets.Background;
import net.cmr.gaze.stage.widgets.WorldWidget;
import net.cmr.gaze.stage.widgets.WorldWidgetGroup;
import net.cmr.gaze.util.GameLoader;

public class SaveSelectScreen implements Screen {

	final Gaze game;
	public final CreationType creationType;
	
	TextButton back, play;
	ImageButton addServer, removeServer;
	Stage centerStage;
	ScrollPane worldPanel;
	WorldWidgetGroup wwg;
	
	public SaveSelectScreen(final Gaze game, CreationType creationType) {
		this.game = game;
		this.creationType = creationType;
		this.centerStage = new Stage();
		centerStage.setViewport(game.viewport);
		
		Gdx.input.setInputProcessor(centerStage);
		
		back = new TextButton("Back", game.getSkin(), "buttonLarge");
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
		centerStage.addActor(back);

		play = new TextButton("Play", game.getSkin(), "buttonLarge");
		play.setPosition(20f+220f, 30, Align.left);
		play.setWidth(200f);
		play.setHeight(50f);
		play.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
		    	if(wwg.getSelectedWidget()!=null) {
		    		game.playSound("trueSelect", 1f);
		    		if(creationType==CreationType.Singleplayer) {
		    			GameLoader.startSingleplayer(game, wwg.getSelectedWidget().toString());
		    		} else {
		    			GameLoader.startMultiplayer(game);
		    		}
		    	} else {
		    		game.playSound("falseSelect", 1f);
		    	}
		    }
		});
		centerStage.addActor(play);
		
		ImageButtonStyle style = new ImageButtonStyle();
		style.over = new TextureRegionDrawable(game.getSprite("plusButtonSelected"));
		style.up = new TextureRegionDrawable(game.getSprite("plusButton"));
		style.checked = new TextureRegionDrawable(game.getSprite("plusButtonSelected"));
		
		//addServer = new ImageButton(new TextureRegionDrawable(game.getSprite("plusButton")), new TextureRegionDrawable(game.getSprite("plusButtonSelected")));
		addServer = new ImageButton(style);
		addServer.setSize(50f, 50f);
		addServer.setPosition(640f-20f-50f-20f-50f, 38, Align.left);
		addServer.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(removeServer.isChecked()) {
					removeServer.setChecked(false);
				}
				game.setScreen(new SaveAddScreen(game, creationType));
				return false;
			}
		});
		
		centerStage.addActor(addServer);
		
		ImageButtonStyle style2 = new ImageButtonStyle();
		style2.over = new TextureRegionDrawable(game.getSprite("minusButtonSelected"));
		style2.up = new TextureRegionDrawable(game.getSprite("minusButton"));
		style2.checked = new TextureRegionDrawable(game.getSprite("minusButtonSelected"));
		
		removeServer = new ImageButton(style2);
		removeServer.setSize(50f, 50f);
		removeServer.setPosition(640f-20f-50f, 38, Align.left);
		removeServer.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(addServer.isChecked()) {
					addServer.setChecked(false);
				}
				
				wwg.deselectAll();
	    		if(removeServer.isChecked()) {
		    		game.playSound("falseSelect", 1f);
		    		for(WorldWidget widg : wwg.getWidgets()) {
		    			widg.clickedOnce = false;
		    			widg.confirmDelete.setText("");
		    		}
	    		} else {
		    		game.playSound("select", 1f);
	    		}
				
				return false;
			}
		});
		centerStage.addActor(removeServer);
		
		
		Table table = new Table();
		ScrollPaneStyle scrollStyle = new ScrollPaneStyle();
		worldPanel = new ScrollPane(table, scrollStyle);
		worldPanel.setScrollbarsVisible(true);
		worldPanel.setBounds(640/2-200, 360/2-100, 400, 200);
		
		centerStage.addActor(worldPanel);
		
		wwg = WorldWidget.createWorldWidgets(game, this);
		for(WorldWidget world : wwg.getWidgets()) {
			table.add(world).width(360).height(60).pad(5).row();
		}
		
		//table.add(new WorldWidget(game, "WORLD")).width(360).height(60).pad(5).row();
		//table.add(new WorldWidget(game, "AmoetwWEr")).width(360).height(60).pad(5).row();
		//table.add(new WorldWidget(game, "wtOOOOOOO")).width(360).height(60).pad(5).row();
		
		
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
		
		game.viewport.apply();
		game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
		game.batch.begin();
		game.getFont(40).draw(game.batch, "Select Save", 30, 360-30);
		centerStage.act(delta);
		centerStage.draw();
		
		if(removeServer.isChecked() && wwg.getSelectedWidget()!=null) {
			wwg.getSelectedWidget().attemptDelete();
		}
		
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		centerStage.getViewport().update(width, height);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		
	}

}

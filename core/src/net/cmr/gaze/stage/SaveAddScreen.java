package net.cmr.gaze.stage;

import java.io.File;
import java.util.Random;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.networking.SaveData;
import net.cmr.gaze.stage.widgets.WorldWidgetGroup;

public class SaveAddScreen implements Screen {

	final Gaze game;
	final CreationType creationType;
	TextButton back, add;
	Stages stages;
	InputMultiplexer multi;
	TextField worldName, worldSeed;
	
	public SaveAddScreen(final Gaze game, CreationType creationType) {
		this.game = game;
		this.creationType = creationType;
		this.stages = new Stages(game);
		multi = stages.getInputMultiplexer();
		
		back = new TextButton("Back", game.getSkin(), "button");
		back.setPosition(20f, 30, Align.left);
		back.setWidth(200f);
		back.setHeight(50f);
		back.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
		    	game.setScreen(new SaveSelectScreen(game, creationType));
		    }
		});
		stages.get(Align.bottom).addActor(back);
		
		add = new TextButton("Add", game.getSkin(), "button");
		add.setPosition(640-20-200, 30, Align.left);
		add.setWidth(200f);
		add.setHeight(50f);
		add.addListener(new ClickListener(){
		    @Override
		    public void clicked(InputEvent event, float x, float y)
		    {
		    	/*String namee = serverName.getText();
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
		    	}*/
		    	
		    	
		    	String worldNameString = worldName.getText();
		    	if(worldNameString.isEmpty()) {
		    		worldNameString = "New Save";
		    	}
		    	
		    	while(true) {
		    		FileHandle handle = Gdx.files.external("/Gaze/saves/"+worldNameString+"/");
		    		if(handle.isDirectory()||handle.exists()) {
		    			worldNameString+="-";
		    		} else {
		    			break;
		    		}
		    	}
		    	
		    	String worldSeedString = worldSeed.getText();
		    	long hash = 0;
		    	if(!worldSeedString.isEmpty()) {
			    	hash = UUID.nameUUIDFromBytes(worldSeedString.getBytes()).getMostSignificantBits();
		    	} else {
		    		hash = new Random().nextLong();
		    	}
		    	
		    	SaveData.writeLocal(worldNameString, hash, 0);
		    	
		    	game.setScreen(new SaveSelectScreen(game, creationType));
		    }
		});
		stages.get(Align.bottom).addActor(add);
		
		
		worldName = new TextField("", game.getSkin(), "textFieldLarge");
		worldName.setBounds(320-41*3, 360-41-100, 82*3, 82);
		worldName.setAlignment(Align.center);
		worldName.setMaxLength(24);
		worldName.setMessageText("World Name");
		worldName.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				
				if(Character.isSpaceChar(c)) {
					return true;
				}
				if(Character.isAlphabetic(c)) {
					return true;
				}
				if(Character.isDigit(c)) {
					return true;
				}
				if(c=='-') {
					return true;
				}
				
				return false;
			}
			
		});
		worldName.setSize(41*6, 41);
		stages.get(Align.center).addActor(worldName);
		
		worldSeed = new TextField("", game.getSkin(), "textFieldLarge");
		worldSeed.setBounds(320-41*3, 360-41-100-41-5, 82*3, 82);
		worldSeed.setAlignment(Align.center);
		worldSeed.setMaxLength(24);
		worldSeed.setMessageText("World Seed");
		worldSeed.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				
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
		worldSeed.setSize(41*6, 41);
		stages.get(Align.center).addActor(worldSeed);
		
		
		Gdx.input.setInputProcessor(multi);
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		stages.get(Align.topLeft).getViewport().apply();
		game.batch.setProjectionMatrix(stages.get(Align.topLeft).getCamera().combined);
		game.batch.begin();
		String title = "Create Save";
		int size = 40;
		float xOffset = (-new GlyphLayout(game.getFont(size), title).width)/2;
		float yOffset = (-new GlyphLayout(game.getFont(size), title).height)/2;
		game.getFont(size).draw(game.batch, title, 640/2+xOffset, 360-30+yOffset);
		stages.act(delta);
		stages.render(game.batch, false);
		
		game.batch.end();
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

}

package net.cmr.gaze.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.cmr.gaze.Gaze;

public class MessageScreen implements Screen {

	final Gaze game;
	final String message;
	TextButton back;
	Stage centerStage;
	
	public MessageScreen(final Gaze game, String message) {
		this.game = game;
		this.message = message;
		this.centerStage = new Stage();
		centerStage.setViewport(game.viewport);
		
		Gdx.input.setInputProcessor(centerStage);
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
		centerStage.addActor(back);
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
		game.getFont(50).draw(game.batch, "Message", 30, 360-30);
		float xOffset = (-new GlyphLayout(game.getFont(10), message).width)/2;
		float yOffset = (-new GlyphLayout(game.getFont(10), message).height)/2;
		game.getFont(10).draw(game.batch, message, 640/2+xOffset, 360/2+yOffset);
		centerStage.act(delta);
		centerStage.draw();
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		centerStage.getViewport().update(width, height);
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
	}

}

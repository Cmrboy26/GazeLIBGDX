package net.cmr.gaze.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
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
	Stages stages;
	
	public MessageScreen(final Gaze game, String message) {
		this.game = game;
		this.message = message;
		this.stages = new Stages(game);
		
		Gdx.input.setInputProcessor(stages.getInputMultiplexer());
		back = new TextButton("Back", game.getSkin(), "button");
		back.setPosition((640-200)/2, 30, Align.left);
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
		stages.get(Align.bottom).addActor(back);

		LabelStyle labelStyle = new LabelStyle(game.getFont(40), Color.WHITE);
		Label title = new Label("Message", labelStyle);
		title.setBounds(0, 360-30-40, 640, 40);
		title.setAlignment(Align.center, Align.center);
		stages.get(Align.top).addActor(title);

		LabelStyle messageLabelStyle = new LabelStyle(game.getFont(20), Color.WHITE);
		Label messageLabel = new Label(message, messageLabelStyle);
		messageLabel.setBounds(0, 360/4, 640, 360/2);
		messageLabel.setAlignment(Align.center, Align.center);
		stages.get(Align.center).addActor(messageLabel);
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		stages.act(delta);
		stages.render(game.batch, true);
	}

	@Override
	public void resize(int width, int height) {
		stages.resize(width, height);
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
		stages.dispose();
	}

}

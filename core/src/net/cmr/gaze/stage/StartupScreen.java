package net.cmr.gaze.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.util.CustomMath;

public class StartupScreen implements Screen {

	final Gaze game;

	Viewport bottom;
	
	Texture bckTx;
	TextureRegion background;
	
	boolean transitioning = false;
	
	public StartupScreen(final Gaze game) {
		this.game = game;
		
		this.bottom = new FitViewport(640, 360);
		bottom.getCamera().position.set(320, 180, 0);
		//bottom.setScreenY(0);
		
		//this.viewport = new FitViewport(640, 360, camera);
		//this.viewport.setWorldSize(640, 360);
		bckTx = new Texture("Background.png");
		bckTx.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
		background = new TextureRegion(bckTx);
		background.setRegion(0, 0, bckTx.getWidth()*6, bckTx.getHeight());
		//background.setRegion(0,0,bckTx.getWidth()*4,bckTx.getHeight()*4);
	}
	
	@Override
	public void show() {
		
	}
	
	float elapsedTime = 0, transitionTime = 0;
	
	@Override
	public void render(float delta) {
		elapsedTime += delta;
		if(Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
			if(!transitioning) {
				game.playSound("intro", 1f);
				game.playSound("trueSelect", 1f);
			}
			transitioning = true;
		}
		if(transitioning) {
			transitionTime += delta;
		}
		if(transitionTime > 1 || Gaze.SKIP) {
			game.setScreen(new MainMenuScreen(game));
		}
		

		game.batch.setProjectionMatrix(bottom.getCamera().combined);
		game.batch.begin();
		bottom.apply();

		float time = 25f;
		float height = 360;
		//game.batch.draw(background, (((elapsedTime/time)*height*3)%(height))-640, 0, height*6, height);
		game.batch.end();
		
		game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
		game.batch.begin();
		game.viewport.apply();
		
		game.batch.draw(game.getSprite("logo"), (640/2)-75, (360/2)-75, 150, 150);
		float dTime = CustomMath.minMax(0f, transitionTime, 1f);
		float g = (float) Math.pow(dTime, -Math.pow((4f*dTime-2/3f),1f));
		float r, b;
		
		r = CustomMath.minMax(0, 1, 1f);
		b = 1-CustomMath.minMax(0, g, 1f);
		g = CustomMath.minMax(0, 1, 1f);
		dTime = Interpolation.swingOut.apply(dTime);
		
		BitmapFont font = game.getFont(24+(transitioning?5:0)-((dTime*5f)));
		font.setColor(r, g, b, 1f);
		font.draw(game.batch, "Press anywhere to begin.", (640/2)-100, 60, 200, 1, false);
		font.setColor(Color.WHITE);
		
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		bottom.update(width, height);
		//bottom.setScreenY(0);
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
		bckTx.dispose();
		background.getTexture().dispose();
	}

}

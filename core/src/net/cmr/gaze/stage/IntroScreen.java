package net.cmr.gaze.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.util.CustomMath;

public class IntroScreen implements Screen {

    final Gaze game;
    Stages stages;
    float elapsedTime = 0;
    Label label;

    public IntroScreen(final Gaze game) {
		this.game = game;
        stages = new Stages();
        LabelStyle style = new LabelStyle();
        style.font = game.getFont(40);
        label = new Label("Cmrboy26", style);
        label.setPosition(360-label.getWidth()/2, 360/2-label.getHeight()/2);
        stages.get(Align.center).addActor(label);
    }

    @Override
    public void show() {
        
    }

    @Override
    public void render(float delta) {   
        elapsedTime += delta;

        // plug in elapsed time into f(x)=-x^3+x^2+x
        float x = elapsedTime/2f;
        float alpha = -x*x*x + x*x + x;
        alpha = CustomMath.minMax(0, alpha, 1);

        game.viewport.apply();
		game.batch.begin();
        game.batch.setColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, alpha);
        label.setColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, alpha);
        stages.act(delta);
        stages.render(game.batch, false);
		game.batch.setProjectionMatrix(stages.get(Align.center).getCamera().combined);
        game.batch.draw(game.getSprite("cmrboy26"), 130, 130, 100, 100);
		game.batch.end();

        if(elapsedTime>=3.3 || Gaze.SKIP) {
            Preferences login = Gdx.app.getPreferences("LoginData");
            game.batch.setColor(Color.WHITE);
            if(login.getString("credentials", null) == null) {
                game.setScreen(new SetupScreen(game));
            } else {
                game.setScreen(new StartupScreen(game));
            }
        }
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

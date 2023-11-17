package net.cmr.gaze.stage;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.util.Debug;

@Debug
public class AlignmentScreen implements Screen {

    Gaze game;
    Stages stages;

    public AlignmentScreen(Gaze game) {
        this.game = game;
        this.stages = new Stages();
        
        Label bottom = new Label("bottom", game.getSkin());
        bottom.setBounds(0, 0, 640, 360);
        bottom.setAlignment(Align.center);
        //stages.get(Align.bottom).addActor(bottom);
        Label bottomLeft = new Label("bottomLeft", game.getSkin());
        bottomLeft.setBounds(0, 0, 640, 360);
        bottomLeft.setAlignment(Align.center);
        bottomLeft.debug();
        stages.get(Align.bottomLeft).addActor(bottomLeft);
        Label bottomRight = new Label("bottomRight", game.getSkin());
        bottomRight.setBounds(0, 0, 640, 360);
        bottomRight.setAlignment(Align.center);
        //stages.get(Align.bottomRight).addActor(bottomRight);
        Label center = new Label("center", game.getSkin());
        center.setBounds(0, 0, 640, 360);
        center.setAlignment(Align.center);
        center.debug();
        stages.get(Align.center).addActor(center);
        Label left = new Label("left", game.getSkin());
        left.setBounds(0, 0, 640, 360);
        left.setAlignment(Align.center);
        //stages.get(Align.left).addActor(left);
        Label right = new Label("right", game.getSkin());
        right.setBounds(0, 0, 640, 360);
        right.setAlignment(Align.center);
        //stages.get(Align.right).addActor(right);
        Label top = new Label("top", game.getSkin());
        top.setBounds(0, 0, 640, 360);
        top.setAlignment(Align.center);
        //stages.get(Align.top).addActor(top);
        Label topLeft = new Label("topLeft", game.getSkin());   
        topLeft.setBounds(0, 0, 640, 360);
        topLeft.setAlignment(Align.center);
        //stages.get(Align.topLeft).addActor(topLeft);
        Label topRight = new Label("topRight", game.getSkin());
        topRight.setBounds(0, 0, 640, 360);
        topRight.setAlignment(Align.center);
        topRight.debug();
        stages.get(Align.topRight).addActor(topRight);
        //stages.setStageZoom(1.5f);
    }

    @Override
    public void show() {

    }

    float elapsed = 0;

    @Override
    public void render(float delta) {
        elapsed += delta;
        float tempZoom = (float) (Math.sin(Math.pow(elapsed, 1.5)));
        tempZoom+=2;
        
        stages.setStageZoom(tempZoom);
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

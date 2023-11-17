package net.cmr.gaze.stage;

import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Stages {
    
    HashMap<Integer, Stage> stages;

    public Stages() {
        // sets the number of stages to 9, the number of Align values in libGDX's Align class
        stages = new HashMap<Integer, Stage>(9);
        initializeStage(Align.topLeft);
        initializeStage(Align.top);
        initializeStage(Align.topRight);
        initializeStage(Align.left);
        initializeStage(Align.center);
        initializeStage(Align.right);
        initializeStage(Align.bottomLeft);
        initializeStage(Align.bottom);
        initializeStage(Align.bottomRight);
    }

    private void initializeStage(int align) {
        Viewport viewport = new FitViewport(640, 360);
        viewport.getCamera().position.set(320, 180, 0);
        Stage stage = new Stage(viewport);
        stages.put(align, stage);
    }

    public void resize(int width, int height) {
        for(Stage stage : stages.values()) {
            stage.getViewport().update(width, height, false);
        }
        get(Align.left);
    }

    public Stage get(int align) {
        return stages.get(align);
    }

}

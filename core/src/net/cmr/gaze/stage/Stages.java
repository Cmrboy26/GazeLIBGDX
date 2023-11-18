package net.cmr.gaze.stage;

import java.util.HashMap;
import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.cmr.gaze.Gaze;

/**
 * The Stages class manages multiple stages for a game or application. 
 * Each stage is associated with an alignment value from libGDX's Align class. 
 * The class provides methods for resizing, zooming, acting, rendering, and disposing of the stages.
 */
public class Stages implements Disposable {
    
    HashMap<Integer, Stage> stages;
    float zoom;

    /**
     * Constructs a new Stages object with 9 stages, one for each alignment value in libGDX's Align class.
     */
    public Stages() {
        this(1f);
    }

    /**
     * Constructs a new Stages object with 9 stages, one for each alignment value in libGDX's Align class.
     */
    public Stages(float zoom) {
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
        setStageZoom(zoom);
    }

    public Stages(Gaze game) {
        this(game.settings.getFloat("uiZoom"));
    }

    /**
     * Initializes a new stage with the given alignment value and adds it to the stages HashMap.
     * @param align the alignment value for the new stage
     */
    private void initializeStage(int align) {
        Viewport viewport = new FitViewport(640, 360);
        Stage stage = new Stage(viewport);
        stages.put(align, stage);
    }

    /**
     * Resizes all stages to the given width and height, and updates their viewport accordingly.
     * @param width the new width of the stages
     * @param height the new height of the stages
     */
    public void resize(int width, int height) {
        for(int align : stages.keySet()) {
            Stage stage = stages.get(align);
            stage.getViewport().update(width, height, true);
            stage.getViewport().setWorldSize(640*zoom, 360*zoom);
            if(Align.isLeft(align)) {
                stage.getViewport().setScreenX(0);
            }
            else if(Align.isRight(align)) {
                stage.getViewport().setScreenX((int)(width - stage.getViewport().getScreenWidth()/zoom));
            } else {
                stage.getViewport().setScreenX((int)((width - stage.getViewport().getScreenWidth()/zoom)/2f));
            }
            if(Align.isTop(align)) {
                stage.getViewport().setScreenY((int)(height - stage.getViewport().getScreenHeight()/zoom));
            }
            else if(Align.isBottom(align)) {
                stage.getViewport().setScreenY(0);
            } else {
                stage.getViewport().setScreenY((int)((height - stage.getViewport().getScreenHeight()/zoom)/2f));
            }
        }
    }

    /**
     * Returns the stage associated with the given alignment value.
     * @param align the alignment value of the desired stage
     * @return the stage associated with the given alignment value
     */
    public Stage get(int align) {
        return stages.get(align);
    }

    /**
     * Sets the zoom of all stages. The zoom is clamped to a minimum of 1.
     * @param zoom the new zoom value for the stages
     */
    public void setStageZoom(float zoom) {
        this.zoom = Math.max(1, zoom);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    } 

    /**
     * Calls act on all stages with the given delta time.
     * @param delta the time elapsed since the last frame
     */
    public void act(float delta) {
        for(int align : stages.keySet()) {
            Stage stage = stages.get(align);
            stage.act(delta);
        }
    }

    /**
     * Renders all stages using the given batch. If endBatch is true, the batch will be ended after rendering.
     * @param batch the batch to use for rendering
     * @param endBatch whether or not to end the batch after rendering
     */
    public void render(Batch batch, boolean endBatch) {
        Objects.requireNonNull(batch);
        if(!batch.isDrawing()) batch.begin();
        for(int align : stages.keySet()) {
            Stage stage = stages.get(align);
            stage.getViewport().apply(false);
            batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
            stage.draw();
        }
        if(endBatch) batch.end();
    }

    public InputMultiplexer getInputMultiplexer() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        for(int align : stages.keySet()) {
            Stage stage = stages.get(align);
            multiplexer.addProcessor(stage);
        }
        return multiplexer;
    }

    /**
     * Disposes of all stages and their associated resources.
     */
    @Override
    public void dispose() {
        for(int align : stages.keySet()) {
            Stage stage = stages.get(align);
            stage.dispose();
        }
    }
}

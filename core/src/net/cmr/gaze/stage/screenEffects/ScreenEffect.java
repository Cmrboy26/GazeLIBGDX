package net.cmr.gaze.stage.screenEffects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;

public abstract class ScreenEffect {
    
    public abstract void render(GameScreen screen, FrameBuffer buffer);

    protected Gaze gaze() {
        return Gaze.get();
    }

    protected SpriteBatch batch() {
        return Gaze.get().batch;
    }

    public void stop(GameScreen screen) {
        screen.removeScreenEffect(this);
    }

}

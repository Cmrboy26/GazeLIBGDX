package net.cmr.gaze.stage.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.world.entities.HealthEntity;
import net.cmr.gaze.world.entities.Player;

public class BarsWidget extends WidgetGroup {
    
    ProgressBar healthBar, foodBar;
    Image healthUp, healthDown, foodUp, foodDown;
    float healthUpTime, healthDownTime, foodUpTime, foodDownTime;

    //constructor
    public BarsWidget(Gaze game) {
        setHeight(21*2);
        setWidth(68*2);
        setPosition(0, 360-getHeight());

        Image background = new Image(game.getSprite("healthbarBackground"));
        background.setBounds(0, -8, 72*2, getHeight()+8);
        addActor(background);

        Image icons = new Image(game.getSprite("barsIcons"));
        icons.setBounds(0, 0, 13*2, getHeight());
        addActor(icons);

        healthUp = new Image(game.getSprite("barIconChanges1")) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, healthUpTime);
            }
        };
        healthUp.setBounds(getWidth()+12, 18, 24*2, 12*2);
        addActor(healthUp);
        healthDown = new Image(game.getSprite("barIconChanges2")) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, healthDownTime);
            }
        };
        healthDown.setBounds(getWidth()+12, 18, 24*2, 12*2);
        addActor(healthDown);

        foodUp = new Image(game.getSprite("barIconChanges3")) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, foodUpTime);
            }
        };
        foodUp.setBounds(getWidth()+12, -2, 24*2, 12*2);
        addActor(foodUp);
        foodDown = new Image(game.getSprite("barIconChanges4")) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, foodDownTime);
            }
        };
        foodDown.setBounds(getWidth()+12, -2, 24*2, 12*2);
        addActor(foodDown);

		float scale = 1f;

        NinePatch patch = new NinePatch(game.bar);
		patch.scale(scale, scale);
		NinePatchDrawable bar = new NinePatchDrawable(patch);
		
		NinePatch patchEMP = new NinePatch(game.barEmpty);
		patchEMP.scale(scale, scale);
		NinePatchDrawable barEMP = new NinePatchDrawable(patchEMP);

		NinePatch patchBCK = new NinePatch(game.barBackground);
		patchBCK.scale(scale, scale);
		NinePatchDrawable barBCK = new NinePatchDrawable(patchBCK);
		
		ProgressBarStyle style = new ProgressBarStyle();
		style.knob = null;
		style.knobBefore = bar;
		//style.knobAfter = barEMP;
		//style.background = barBCK;

        Interpolation visualInterpolation = Interpolation.pow3Out;
		float animationDuration = 0.5f;
		
        
        Image healthBarBackground = new Image(barBCK);
        healthBarBackground.setBounds(13*2, 13*2-1, 54*2, 10);
        addActor(healthBarBackground);
		healthBar = new ProgressBar(0, 1, .01f, false, style);
		healthBar.setTouchable(Touchable.disabled);
		healthBar.setColor(Color.RED);
		healthBar.setBounds(13*2, 13*2-1, 54*2, 10);
		healthBar.setValue(1f);
		healthBar.setAnimateInterpolation(visualInterpolation);
		healthBar.setAnimateDuration(animationDuration);
		addActor(healthBar);

        
        Image foodBarBackground = new Image(barBCK);
        foodBarBackground.setBounds(13*2, 3*2-1, 38*2, 8);
        addActor(foodBarBackground);
        foodBar = new ProgressBar(0, 1, .01f, false, style);
		foodBar.setTouchable(Touchable.disabled);
        foodBar.setColor(Color.valueOf("bf6e28"));
		foodBar.setBounds(13*2, 3*2-1, 38*2, 8);
		foodBar.setValue(1f);
		foodBar.setAnimateInterpolation(visualInterpolation);
		foodBar.setAnimateDuration(animationDuration);
		addActor(foodBar);

        

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(healthBar.getVisualPercent() < .05f) {
            healthBar.setVisible(false);
        } else {
            healthBar.setVisible(true);
        }

        if(foodBar.getVisualPercent() < .05f) {
            foodBar.setVisible(false);
        } else {
            foodBar.setVisible(true);
        }

        healthUpTime-=delta;
        healthDownTime-=delta;
        foodUpTime-=delta;
        foodDownTime-=delta;
    }

    public void setHealth(int health, int maxHealth) {
        float oldHealth = healthBar.getValue()*maxHealth;
        if(health > oldHealth) {
            healthUpTime = 1;
        } else if(health < oldHealth) {
            healthDownTime = 1;
        }
        healthBar.setValue((float)health/(float)maxHealth);
    }
    public void setFood(float food) {
        float oldFood = foodBar.getValue();
        float difference = food - oldFood;
        if(difference > .02f) {
            foodUpTime = 1;
        } else if(difference < -.02f) {
            foodDownTime = 1;
        }
        foodBar.setValue(food);
    }

}

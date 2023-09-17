package net.cmr.gaze.stage.widgets;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.util.CustomMath;

public class Notification extends WidgetGroup {

	Gaze game;
	Label label;
	Image image;
	String[] text, sprites;
	String soundEffect;
	
	final float textChangeTime, imageChangeTime, totalTime;
	float deltaTime, shakeDelta;
	Vector2 originalPosition = null, textPosition = null;
	Interpolation interpolate;
	float direction = 1;
	boolean finished = false;
	boolean[] animation = null;
	
	public Notification(Gaze game, String[] text, String[] sprites, boolean[] animation, float textChangeTime, float imageChangeTime, float totalTime, String soundEffect) {
		this(game, text, sprites, textChangeTime, imageChangeTime, totalTime, soundEffect);
		this.animation = animation;
	}
	
	public Notification(Gaze game, String[] text, String[] sprites, float textChangeTime, float imageChangeTime, float totalTime, String soundEffect) {
		this.game = game;
		this.text = text;
		this.sprites = sprites;
		this.textChangeTime = textChangeTime;
		this.imageChangeTime = imageChangeTime;
		this.totalTime = totalTime;
		this.interpolate = Interpolation.smooth;
		this.soundEffect = soundEffect;
		
		if(text==null) {
			throw new NullPointerException("Text array is null for notification object.");
		}
		if(text!=null&&text.length==0) {
			throw new IndexOutOfBoundsException();
		}
		
		setWidth(75*2);
		setHeight(22*2);
		
		image = new Image(getBackground(game));
		image.setWidth(75*2);
		image.setHeight(22*2);
		
		addActor(image);
		
		LabelStyle descStyle = new LabelStyle();
		descStyle.font = game.getFont(8);
		descStyle.fontColor = Color.WHITE;
		
		label = new Label(text[0], descStyle);
		label.setBounds(0+28+12, 0, 75*2-28-12, 22*2);
		label.setAlignment(Align.center);
		
		addActor(label);
		
	}
	
	private static TextureRegionDrawable backgroundFilled;
	private static TextureRegionDrawable getBackground(Gaze game) {
		if(backgroundFilled == null) {
			backgroundFilled = new TextureRegionDrawable(game.getSprite("notification"));
		}
		return backgroundFilled;
	}
	
	public void setDirection(boolean right) {
		this.direction = right?1f:-1f;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		
		if(originalPosition==null) {
			originalPosition = new Vector2(getX(), getY());
			textPosition = new Vector2(label.getX(), label.getY());
			game.playSound(soundEffect, 1);
		}
		
		deltaTime+=Gdx.graphics.getDeltaTime();
		
		setX(originalPosition.x+(direction==-1?getWidth()*direction:0)-interpolate.apply(CustomMath.minMax(0f, deltaTime, 1f))*getWidth()*direction);
		
		image.setWidth(75*2*direction);
		image.setHeight(22*2);
		image.setX(direction==1?0:Math.abs(getWidth()));
		
		Color originalColor = batch.getColor();
		
		float alpha = CustomMath.minMax(0f, (totalTime-deltaTime)*1f, 1f);
		batch.setColor(1f, 1f, 1f, alpha);
		
		int textIndex = (int) Math.min(Math.floor((float) (deltaTime/textChangeTime)), this.text.length-1);
		int imageIndex = (int) Math.min(Math.floor((float) (deltaTime/imageChangeTime)), this.sprites.length-1);
		
		/*int lastDifference = ((int) Math.floor(((deltaTime-Gdx.graphics.getDeltaTime())/textChangeTime)%textChangeTime));
		int difference = ((int) Math.floor((deltaTime/textChangeTime)%textChangeTime));
		if(lastDifference!=difference) {
			
		}*/
		
		final float slope = 1/2.5f;
		float v = Math.min(1f, (deltaTime/slope)%(textChangeTime/slope));
		
		v = Interpolation.pow3InInverse.apply(v);
		v = v-1;
		
		Random random = new Random((int) Math.floor(deltaTime*60f));
		final float scale = 5;
		
		random.nextFloat();
		float rX = random.nextFloat();
		float rY = random.nextFloat();
		
		float nX = (rX-.5f)*v*scale*2f;
		float nY = (rY-.5f)*v*scale;
		
		label.setPosition(nX+textPosition.x, nY+textPosition.y);

		
		label.setText(this.text[textIndex]);
		
		super.draw(batch, alpha);
		
		if(animation==null || !animation[imageIndex]) {
			batch.draw(game.getSprite(this.sprites[imageIndex]), getX()+12, (44-26)/2f+getY(), 28, 28);
		} else {
			if(animation[imageIndex]) {
				batch.draw(game.getAnimation(this.sprites[imageIndex]).getKeyFrame(deltaTime-imageIndex*imageChangeTime, true), getX()+12, (44-26)/2f+getY(), 28, 28);
			}
		}
		
		
		batch.setColor(originalColor);
		
		if(alpha<=0) {
			finished = true;
			addAction(Actions.removeActor());
		}
	}
	
	public static float getEvenlySpacedTime(String[] spriteName, float totalDuration) {
		return (float) spriteName.length/totalDuration;
	}

	public boolean finished() {
		return finished;
	}
	
	
}

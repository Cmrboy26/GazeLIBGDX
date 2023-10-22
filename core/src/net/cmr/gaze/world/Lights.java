package net.cmr.gaze.world;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import net.cmr.gaze.Gaze;

public class Lights {
	
	private ArrayList<Float[]> lightData;

	Sprite lighting;
	
	public Lights(Gaze game) {
		this.lightData = new ArrayList<>();
		this.lighting = new Sprite(game.getSprite("lighting"));
	}
	
	public void renderLights(Batch batch) {
		for(Float[] data : lightData) {
			if(data.length==3) {
				batch.setColor(Color.WHITE);
			} else if(data.length==6) {
				batch.setColor(data[3], data[4], data[5], 1f);
			} else {
				batch.setColor(data[3], data[4], data[5], data[6]);
			} 

			float x = data[0]-data[2]/2f;
			float y = data[1]-data[2]/2f;
			batch.draw(lighting, x, y, data[2], data[2]);
		}
		batch.setColor(Color.WHITE);
		lightData.clear();
	}
	
	public void addLight(float x, float y, float intensity, Color color) {
		if(color.equals(Color.WHITE)) {
			addLight(x, y, intensity);
			return;
		}
		Float[] add;
		if(color.a==1) {
			add = new Float[] {x, y, intensity, color.r, color.g, color.b};
		} else {
			add = new Float[] {x, y, intensity, color.r, color.g, color.b, color.a};
		}
		lightData.add(add);
	}
	
	public void addLight(float x, float y, float intensity) {
		Float[] add = new Float[] {x, y, intensity};
		lightData.add(add);
	}
	
}

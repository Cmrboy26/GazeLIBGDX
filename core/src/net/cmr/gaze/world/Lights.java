package net.cmr.gaze.world;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import net.cmr.gaze.util.Vector2Double;

public class Lights {
	
	private ArrayList<Float[]> lightData;

	Sprite lighting;
	Texture lightTexture;
	
	public Lights() {
		this.lightData = new ArrayList<>();
		this.lightTexture = new Texture("atlas/atlasSprites/lighting.png");
		this.lighting = new Sprite(lightTexture);
	}
	
	public void renderLights(Batch batch) {
		for(Float[] data : lightData) {
			float x = data[0]-data[2]/2f;
			float y = data[1]-data[2]/2f;
			batch.draw(lighting, x, y, data[2], data[2]);
		}
		lightData.clear();
	}
	
	public void addLight(float x, float y, float intensity) {
		Float[] add = new Float[] {x, y, intensity};
		lightData.add(add);
	}
	
}

package net.cmr.gaze.stage.widgets;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Background {

	static float widgetDelta = 100;
	static final float widgetDuration = 10;
	static int backgroundNumber;
	static int backgroundImageAmounts;
	
	static int direction = -1;
	
	static Texture bckTx;
	static TextureRegion background;
	
	/*public BackgroundWidget(Gaze game) {
		this.game = game;
		for(int i = 0; true; i++) {
			if(Gdx.files.internal("backgrounds/background"+i+".png").exists()) {
				continue;
			}
			backgroundImageAmounts = i;
			break;
		}
	}
	
	private static BackgroundWidget singleton;
	
	public static BackgroundWidget get(Gaze game) {
		if(singleton == null) {
			singleton = new BackgroundWidget(game);
		}
		return singleton;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		float delta = Gdx.graphics.getDeltaTime();
		widgetDelta+=delta;
		
		if(widgetDelta>widgetDuration) {
			int lastNumber = backgroundNumber;
			
			while(backgroundNumber==lastNumber) {
				if(backgroundImageAmounts == 1) {
					break;
				}
				backgroundNumber = new Random().nextInt(backgroundImageAmounts);
			}
			
			if(bckTx!=null) {
				bckTx.dispose();
				background.getTexture().dispose();
				background = null;
				bckTx = null;
			}
			
			direction*=-1;
			
			widgetDelta = 0;
		}
		
		if(background == null) {
			bckTx = new Texture("backgrounds/background"+backgroundNumber+".png");
			//bckTx.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
			background = new TextureRegion(bckTx);
			//background.setRegion(0, 0, bckTx.getWidth(), bckTx.getHeight());
		}
		
		//System.out.println(bckTx.getWidth()+","+bckTx.getHeight());
		float aspectRatio = (float) bckTx.getWidth()/bckTx.getHeight();
		float width = aspectRatio*360f;
		
		batch.draw(background, getX()-(width-640f)/2f+(widgetDelta*2f*direction), getY(), width, 360);
	}*/
	
	static boolean initialized = false;
	
	public static void initialize() {
		for(int i = 0; true; i++) {
			if(Gdx.files.internal("backgrounds/background"+i+".png").exists()) {
				continue;
			}
			backgroundImageAmounts = i;
			break;
		}
		initialized = true;
	}
	
	public static void draw(Batch batch, Viewport v) {
		if(!initialized) {
			initialize();
		}
		float delta = Gdx.graphics.getDeltaTime();
		widgetDelta+=delta;
		
		if(widgetDelta>widgetDuration) {
			int lastNumber = backgroundNumber;
			
			while(backgroundNumber==lastNumber) {
				if(backgroundImageAmounts == 1) {
					break;
				}
				backgroundNumber = new Random().nextInt(backgroundImageAmounts);
			}
			
			if(bckTx!=null) {
				bckTx.dispose();
				background.getTexture().dispose();
				background = null;
				bckTx = null;
			}
			
			direction*=-1;
			
			widgetDelta = 0;
		}
		
		if(background == null) {
			bckTx = new Texture("backgrounds/background"+backgroundNumber+".png");
			//bckTx.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
			background = new TextureRegion(bckTx);
			//background.setRegion(0, 0, bckTx.getWidth(), bckTx.getHeight());
		}

		//float off = v.unproject(new Vector2(0,Gdx.graphics.getHeight()/2)).x;
		float height = v.getWorldHeight();
		
		float aspectRatio = (float) bckTx.getWidth()/bckTx.getHeight();
		float width = aspectRatio*height;
		Vector2 pos = v.unproject(new Vector2(0, Gdx.graphics.getHeight()));
		
		float off = Math.max(0, (-v.getWorldWidth()+width)/2f);
		
		batch.draw(background, (widgetDelta*2f*direction)+pos.x-off, pos.y, width, height);
	}
	
}

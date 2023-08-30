package net.cmr.gaze.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.entities.Player;

public class PlayerDisplayWidget extends Image {

	Gaze game;
	GameScreen scene;
	String lastAnimation;
	Player player;
	
	public PlayerDisplayWidget(Gaze game, GameScreen scene) {
		super();
		this.game = game;
		this.scene = scene;
	}
	public PlayerDisplayWidget(Gaze game, Player player) {
		super();
		this.game = game;
		this.player = player;
	}
	
	float stateTime;
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(scene!=null) {
			player = scene.getLocalPlayer();
		}
		if(player!=null) {
			stateTime += Gdx.graphics.getDeltaTime();
			if(!player.getAnimationString().equals(lastAnimation)) {
				stateTime = 0;
				lastAnimation = player.getAnimationString();
			}
			
			TextureRegion region = game.getAnimation(lastAnimation).getKeyFrame(stateTime, true);
			this.setDrawable(new TextureRegionDrawable(region));
		}
		super.draw(batch, parentAlpha);
	}
	
}

package net.cmr.gaze.leveling;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.leveling.Skills.Skill;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.entities.Player;

public class SkillDisplay extends WidgetGroup {

	Gaze game;
	GameScreen screen;
	
	final int x, y;
	final int spacing = 5*2;
	
	ProgressBar combat, fishing, foraging, mining, crafting;
	
	public SkillDisplay(Gaze game, GameScreen screen) {
		this.game = game;
		this.screen = screen;
		
		x = 256*2;
		y = 148*2;
		
		Image image = new Image(game.getSprite("levelBackground"));
		image.setBounds(x-8*2, y, 72*2, 32*2);
		addActor(image);
		
		Image icons = new Image(game.getSprite("levelIcons"));
		icons.setBounds(x+10, y+10, 4*2, 25*2);
		addActor(icons);
		
		float scale = .6f;
		
		NinePatch patch = new NinePatch(game.bar);
		patch.scale(scale, scale);
		NinePatchDrawable bar = new NinePatchDrawable(patch);
		
		NinePatch patchBCK = new NinePatch(game.barBackground);
		patchBCK.scale(scale, scale);
		NinePatchDrawable barBCK = new NinePatchDrawable(patchBCK);
		
		ProgressBarStyle style = new ProgressBarStyle();
		style.knob = null;
		style.knobBefore = bar;
		style.knobAfter = barBCK;
		//style.background = barBCK;
		
		Interpolation visualInterpolation = Interpolation.pow3Out;
		float animationDuration = 1.5f;
		
		combat = new ProgressBar(0, 1, .01f, false, style);
		combat.setTouchable(Touchable.disabled);
		combat.setColor(Color.RED);
		combat.setBounds(x+20, y+10, 52*2, 8);
		combat.setValue(0f);
		combat.setAnimateInterpolation(visualInterpolation);
		combat.setAnimateDuration(animationDuration);
		addActor(combat);
		
		fishing = new ProgressBar(0, 1, .01f, false, style);
		fishing.setTouchable(Touchable.disabled);
		fishing.setColor(Color.BLUE);
		fishing.setBounds(x+20, y+10+spacing, 52*2, 8);
		fishing.setValue(0f);
		fishing.setAnimateInterpolation(visualInterpolation);
		fishing.setAnimateDuration(animationDuration);
		addActor(fishing);
		
		mining = new ProgressBar(0, 1, .01f, false, style);
		mining.setTouchable(Touchable.disabled);
		mining.setColor(Color.LIGHT_GRAY);
		mining.setBounds(x+20, y+10+spacing+spacing, 52*2, 8);
		mining.setValue(0f);
		mining.setAnimateInterpolation(visualInterpolation);
		mining.setAnimateDuration(animationDuration);
		addActor(mining);
		
		foraging = new ProgressBar(0, 1, .01f, false, style);
		foraging.setTouchable(Touchable.disabled);
		foraging.setColor(Color.GREEN);
		foraging.setBounds(x+20, y+10+spacing+spacing+spacing, 52*2, 8);
		foraging.setValue(0f);
		foraging.setAnimateInterpolation(visualInterpolation);
		foraging.setAnimateDuration(animationDuration);
		addActor(foraging);
		
		crafting = new ProgressBar(0, 1, .01f, false, style);
		crafting.setTouchable(Touchable.disabled);
		crafting.setColor(Color.YELLOW);
		crafting.setBounds(x+20, y+10+spacing+spacing+spacing+spacing, 52*2, 8);
		crafting.setValue(0f);
		crafting.setAnimateInterpolation(visualInterpolation);
		crafting.setAnimateDuration(animationDuration);
		addActor(crafting);
		
		updateValues();

	}
	
	float localDelta = 0;
	
	@Override
	public void act(float delta) {
		super.act(delta);
		localDelta+=delta;
		if(localDelta>=.1) {
			localDelta=0;
			updateValues();
		}
	}
	
	int combatLevel, fishingLevel, miningLevel, foragingLevel, craftingLevel;
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		
		batch.setColor(Color.WHITE);
		
		if(combatLevel >= 10) batch.draw(game.getSprite("smallNumber"+((int) (combatLevel%100)/10)), x-6, y+10, 3*2, 4*2);
		batch.draw(game.getSprite("smallNumber"+((int) (combatLevel)%10)), x+1, y+10, 3*2, 4*2);
		
		if(fishingLevel >= 10) batch.draw(game.getSprite("smallNumber"+((int) (fishingLevel%100)/10)), x-6, y+10+spacing, 3*2, 4*2);
		batch.draw(game.getSprite("smallNumber"+((int) (fishingLevel)%10)), x+1, y+10+spacing, 3*2, 4*2);
		
		if(miningLevel >= 10) batch.draw(game.getSprite("smallNumber"+((int) (miningLevel%100)/10)), x-6, y+10+spacing+spacing, 3*2, 4*2);
		batch.draw(game.getSprite("smallNumber"+((int) (miningLevel)%10)), x+1, y+10+spacing+spacing, 3*2, 4*2);
		
		if(foragingLevel >= 10) batch.draw(game.getSprite("smallNumber"+((int) (foragingLevel%100)/10)), x-6, y+10+spacing+spacing+spacing, 3*2, 4*2);
		batch.draw(game.getSprite("smallNumber"+((int) (foragingLevel)%10)), x+1, y+10+spacing+spacing+spacing, 3*2, 4*2);
		
		if(craftingLevel >= 10) batch.draw(game.getSprite("smallNumber"+((int) (craftingLevel%100)/10)), x-6, y+10+spacing+spacing+spacing+spacing, 3*2, 4*2);
		batch.draw(game.getSprite("smallNumber"+((int) (craftingLevel)%10)), x+1, y+10+spacing+spacing+spacing+spacing, 3*2, 4*2);
		
	}
	
	public void updateValues() {
		Player local = screen.getLocalPlayer();
		if(local!=null) {
			final float minThres = 0.025f;
			//final float animDur = .1f;
			
			combat.setValue(Math.max(minThres, local.getSkills().getProgress(Skill.COMBAT)));
			//combat.setAnimateDuration(animDur);
			combatLevel = local.getSkills().getLevel(Skill.COMBAT);
			fishing.setValue(Math.max(minThres, local.getSkills().getProgress(Skill.FISHING)));
			//fishing.setAnimateDuration(animDur);
			fishingLevel = local.getSkills().getLevel(Skill.FISHING);
			mining.setValue(Math.max(minThres, local.getSkills().getProgress(Skill.MINING)));
			//mining.setAnimateDuration(animDur);
			miningLevel = local.getSkills().getLevel(Skill.MINING);
			foraging.setValue(Math.max(minThres, local.getSkills().getProgress(Skill.FORAGING)));
			//foraging.setAnimateDuration(animDur);
			foragingLevel = local.getSkills().getLevel(Skill.FORAGING);
			crafting.setValue(Math.max(minThres, local.getSkills().getProgress(Skill.CRAFTING)));
			//crafting.setAnimateDuration(animDur);
			craftingLevel = local.getSkills().getLevel(Skill.CRAFTING);
		}
	}
	
}

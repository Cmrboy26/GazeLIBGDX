package net.cmr.gaze.stage.menus;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.stage.MainMenuScreen;

public class PauseMenu extends GameMenu {

	Gaze game;
	GameScreen screen;
	
	public PauseMenu(Gaze game, GameScreen screen) {
		super(MenuAlignment.CENTER);
		this.game = game;
		this.screen = screen;
		
		Image image = new Image(game.getSprite("pauseMenu"));
		image.setBounds(92*2, 22*2, 136*2, 136*2);
		
		
		LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = game.getFont(20f);
		Label label = new Label("MENU", labelStyle);
		
		int width = 100;
		label.setBounds(92*2+136-width/2, 22*2+(136/.8f), width, 30);
		label.setAlignment(Align.center);
		
		TextButtonStyle style = new TextButtonStyle(game.getSkin().get("button", TextButtonStyle.class));
		style.font = game.getFont(15);
		
		TextButton exit = new TextButton("Quit", style);
		exit.setBounds(92*2+49*2-10, 22*2+43*2, 100, 25);
		exit.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(exit.isPressed()) {
					game.stopAllSounds();
					screen.closeNetworkFeatures();
					game.setScreen(new MainMenuScreen(game));
					game.playSound("falseSelect", 1f);
				}
			}
		});
		
		TextButton resume = new TextButton("Resume", style);
		resume.setBounds(92*2+49*2-10, 22*2+43*2+40, 100, 25);
		resume.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(resume.isPressed()) {
					PauseMenu.this.setVisible(false);
					game.playSound("trueSelect", 1f);
				}
			}
		});
		
		
		addActor(image);
		addActor(label);
		addActor(exit);
		addActor(resume);
	}

	@Override
    public int getOpenKey() {
        return Input.Keys.ESCAPE;
    }

	@Override
	public boolean openFromBlankScreenOnly() {
		return true;
	}
	
}

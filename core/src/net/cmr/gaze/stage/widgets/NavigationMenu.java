package net.cmr.gaze.stage.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.cmr.gaze.Gaze;

public class NavigationMenu extends WidgetGroup {

	Gaze game;
	Menu rootMenu;
	Menu selectedMenu;
	
	public int fontSize = 15;
	
	public NavigationMenu(Gaze game) {
		this.game = game;
		
		//Menu[] menus = new Menu[] {new Menu(game, this, "Play", null), new Menu(game, this, "Settings", null), new Menu(game, this, "Exit", null)};
		

		int width = 230;
		
		Table table3 = new Table();
		table3.add(new Menu(game, this, "wer", null, 20, width)).width(width).height(40).row();
		TextButton button = new TextButton("e", game.getSkin().get("button", TextButtonStyle.class));
		addActor(button);
		table3.add(button).width(100).height(40).row();
		Table table2 = new Table();
		table2.add(new Menu(game, this, "Singleplayer", table3, 20, width)).width(width).height(40).row();
		table2.add(new Menu(game, this, "Multiplayer", null, 20, width)).width(width).height(40).row();
		Table table = new Table();
		table.add(new Menu(game, this, "Play", table2)).width(width).height(40).row();
		table.add(new Menu(game, this, "Settings", null)).width(width).height(40).row();
		table.add(new Menu(game, this, "Credits", null)).width(width).height(40).row();
		table.add(new Menu(game, this, "Exit", null)).width(width).height(40).row();
		
		this.rootMenu = new Menu(game, this, table);
		addActor(rootMenu);
		
		setPosition(320, 180);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		//validate();
		///rootMenu.setPosition(getX(), getY());
		//rootMenu.draw(batch, parentAlpha);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
	}
	
}

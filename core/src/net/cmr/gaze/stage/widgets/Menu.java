package net.cmr.gaze.stage.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.cmr.gaze.Gaze;

public class Menu extends WidgetGroup {

	Gaze game;
	NavigationMenu nav;
	String menuLabel;
	boolean root;
	Table table;
	float yOffset = 0, xOffset = 0;

	public Menu(Gaze game, NavigationMenu nav, String menuLabel, Table table) {
		this.game = game;
		this.nav = nav;
		this.menuLabel = menuLabel;
		this.table = table;
		this.root = false;
	}
	
	public Menu(Gaze game, NavigationMenu nav, String menuLabel, Table table, float yOffset, float xOffset) {
		this(game, nav, menuLabel, table);
		this.yOffset = yOffset;
		this.xOffset = xOffset;
	}
	
	public Menu(Gaze game, NavigationMenu nav, Table table) {
		this(game, nav, "", table);
		this.root = true;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		validate();
		if(!root) {
			//GlyphLayout layout = new GlyphLayout(game.getFont(20), menuLabel);
			//if(menuLabel.equals("Singleplayer")) {
				//System.out.println(getX());
			//}
			game.getFont(nav.fontSize).draw(batch, "> "+menuLabel, getX()+xOffset, getY()+yOffset);
			drawTable(batch, parentAlpha);
		} else {
			drawTable(batch, parentAlpha);
		}
	}
	
	@Override
	public void act(float delta) {
		System.out.println(menuLabel);
		if(!root) {
			actTable(delta);
		} else {
			actTable(delta);
		}
	}
	
	private void actTable(float delta) {
		if(table == null) {
			return;
		}
		table.setPosition(getX()+xOffset, getY()-table.getHeight()/2+yOffset);
		table.act(delta);
	}
	
	private void drawTable(Batch batch, float parentAlpha) {
		if(table == null) {
			return;
		}
		table.setPosition(getX()+xOffset, getY()-table.getHeight()/2+yOffset);
		table.draw(batch, parentAlpha);
	}
	
}

package net.cmr.gaze.stage.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.InventoryGroup;
import net.cmr.gaze.stage.GameScreen;

public abstract class AbstractInventoryWidget extends WidgetGroup {

	public Gaze game;
	public InventoryGroup inventoryGroup;
	public GameScreen screen;
	
	public AbstractInventoryWidget(Gaze game, GameScreen screen, String background) {
		this.game = game;
		this.screen = screen;
		this.inventoryGroup = new InventoryGroup(screen);
		inventoryGroup.setMinCheckCount(0);
		inventoryGroup.setMaxCheckCount(1);
		
		Image image = new Image(game.getSprite(background));
		image.setBounds(320/2, (360-256)/2, 320, 256);
		addActor(image);
		addActor(createInventoryTable());
	}
	
	public abstract Table createInventoryTable();
	
}

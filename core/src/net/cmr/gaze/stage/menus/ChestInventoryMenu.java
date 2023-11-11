package net.cmr.gaze.stage.menus;

import com.badlogic.gdx.Input;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.stage.widgets.ChestInventoryWidget;

public class ChestInventoryMenu extends GameMenu {

    public ChestInventoryMenu(Gaze game, GameScreen screen) {
        super(MenuAlignment.CENTER);
        addActor(new ChestInventoryWidget(game, screen));
    }
    
    public ChestInventoryWidget getInventoryWidget() {
        return (ChestInventoryWidget) getChildren().get(0);
    }

    @Override
    public int getOpenKey() {
        return Input.Keys.E;
    }

    @Override
    public boolean isPopUpMenu() {
        return true;
    }

}

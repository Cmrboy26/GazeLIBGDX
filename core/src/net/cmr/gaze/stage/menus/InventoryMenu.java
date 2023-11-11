package net.cmr.gaze.stage.menus;

import com.badlogic.gdx.Input;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.stage.widgets.PlayerInventoryWidget;

public class InventoryMenu extends GameMenu {

    public InventoryMenu(Gaze game, GameScreen screen) {
        super(MenuAlignment.CENTER);
        addActor(new PlayerInventoryWidget(game, screen));
    }

    public PlayerInventoryWidget getInventoryWidget() {
        return (PlayerInventoryWidget) getChildren().get(0);
    }

    @Override
    public int getOpenKey() {
        return Input.Keys.E;
    }
    
}

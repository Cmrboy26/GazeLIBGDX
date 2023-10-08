package net.cmr.gaze.stage.widgets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.cmr.gaze.stage.GameScreen;

public class GameMenuIcon extends WidgetGroup {

    public static final String INVENTORY_ICON = "UI-Icons1";
    public static final String CRAFTING_ICON = "UI-Icons2";
    public static final String QUESTS_ICON = "UI-Icons3";

    GameScreen screen;

    public GameMenuIcon(GameScreen screen, String iconName, float positionX, float positionY, float dimension) {
        this.screen = screen;
        setBounds(positionX, positionY, dimension, dimension);
        ImageButtonStyle style = new ImageButtonStyle();
        TextureRegionDrawable image = new TextureRegionDrawable(new TextureRegion(screen.game.getSprite(iconName)));
        image.setMinWidth(dimension);
        image.setMinHeight(dimension);
        style.imageUp = image;
        style.imageDown = image;
        style.imageOver = image;

        ImageButton button = new ImageButton(style);
        button.setFillParent(true);
        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
                onClick();
                return false;
            }
        });
        addActor(button);
    }

    public void onClick() {}
    
}

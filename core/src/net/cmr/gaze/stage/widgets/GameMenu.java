package net.cmr.gaze.stage.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public abstract class GameMenu extends WidgetGroup{
    
    boolean visible = false;
    MenuAlignment alignment;
    public GameMenu(MenuAlignment alignment) {
        this.alignment = alignment;
    }

    public static enum MenuAlignment {
        CENTER,
    }
    public MenuAlignment getAlignment() {
        return alignment;
    }

    public abstract WidgetGroup getWidgetGroup();

    protected abstract void setInternalVisibility(boolean visible);
    public final void setMenuVisiblity(boolean visible) {
        this.visible = visible;
        setInternalVisibility(visible);
    }
    public final void toggleMenuVisibility() {
        this.visible = !visible;
        setInternalVisibility(visible);
    }
    public boolean getMenuVisibility() {
        return visible;
    }

}

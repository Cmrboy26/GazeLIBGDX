package net.cmr.gaze.stage.menus;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * A menu that can be opened in the game. This is a widget group, so it can be added to the stage.
 */
public abstract class GameMenu extends WidgetGroup{
    
    boolean visible = true;
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

    /**
     * Returns the key that opens this menu. If this menu is not openable via a keypress, return -1.
     * Use values from {@link com.badlogic.gdx.Input.Keys}
     * @return the key that opens this menu, or -1 if it is not openable via a keypress.
     */
    public int getOpenKey() {
        return -1;
    }

    /**
     * If this is true, the menu can only be opened from a blank menu. This is used for menus such as the pause menu.
     * @return true if this menu can only be opened from a blank menu.
     */
    public boolean openFromBlankScreenOnly() {
        return false;
    }

    /**
     * If this is true, the menu will be closed when the player clicks the open key. 
     * This is useful for scenarios such as the player inventory and chest inventory: by setting the chest inventory to a popup menu,
     * the player can close the chest menu without accidentally opening the player inventory.
     * @return true if this menu is a popup menu.
     */
    public boolean isPopUpMenu() {
        return false;
    }

    // Overridable, but not public. Use setMenuVisibility() instead.
    protected void setInternalVisibility(boolean visible) {
        this.setVisible(visible);
    }
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

    public GameMenuIcon getIcon() {
        // TODO: Implement this. The icon here should get added to the sidebar of the game.
        return null;
    }

}

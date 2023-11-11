package net.cmr.gaze.stage.menus;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.crafting.CategoryButton;
import net.cmr.gaze.crafting.CraftDisplay;
import net.cmr.gaze.crafting.Crafting;
import net.cmr.gaze.crafting.Crafting.CraftingStation;
import net.cmr.gaze.crafting.RecipeCategory;
import net.cmr.gaze.crafting.RecipeDisplay;
import net.cmr.gaze.stage.GameScreen;

public class CraftingMenu extends GameMenu {

	RecipeDisplay recipeDisplay;
	CraftDisplay craftDisplay;
	ScrollPane categoryScrollPane;
	Image craftingLeft, craftingRight;
	ButtonGroup<CategoryButton> categoryButtonGroup;
	Table categoryTable;
    Gaze game;
    GameScreen screen;

    public CraftingMenu(Gaze game, GameScreen screen) {
        super(MenuAlignment.CENTER);
        this.game = game;
        this.screen = screen;
        craftingLeft = new Image(game.getSprite("craftingLeft"));
		craftingLeft.setBounds(0, (360-256)/2, 80*2, 128*2);
		addActor(craftingLeft);
		
		craftingRight = new Image(game.getSprite("craftingRight")) {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				if(recipeDisplay.getSelectedRecipe()!=null) {
					super.draw(batch, parentAlpha);
				}
			}
		};
		craftingRight.setBounds(640-160, (360-256)/2, 80*2, 128*2);
		addActor(craftingRight);

		categoryTable = new Table();
		categoryScrollPane = new ScrollPane(categoryTable);
		categoryButtonGroup = new ButtonGroup<>();
		
		categoryButtonGroup.setMaxCheckCount(1);
		categoryButtonGroup.setMinCheckCount(1);
		
		categoryScrollPane.setPosition(6+28, 319, Align.left);
		categoryScrollPane.setWidth(22*4+2*3);
		categoryScrollPane.setHeight(15);
		categoryScrollPane.setSmoothScrolling(false);
		categoryScrollPane.setOverscroll(false, false);
		
		for(String key : Crafting.getAllCategories().keySet()) {
			RecipeCategory category = Crafting.getAllCategories().get(key);
			CategoryButton button = new CategoryButton(game, category, true);
			categoryButtonGroup.add(button);
			categoryTable.add(button).width(15).height(15).spaceRight(2);
		}
		
		ScrollPaneStyle scrollStyle = new ScrollPaneStyle();
		recipeDisplay = new RecipeDisplay(game, screen, new Table(), scrollStyle, categoryButtonGroup);

		craftDisplay = new CraftDisplay(game, screen, recipeDisplay);

        addActor(recipeDisplay);
        addActor(craftDisplay);
        addActor(categoryScrollPane);
    }

    @Override
    public int getOpenKey() {
        return Input.Keys.C;
    }

    public void updateRecipeDisplay() {
		recipeDisplay.update();
    }

    public void setCraftingStation(CraftingStation station) { 
		recipeDisplay.setCraftingStation(station);
		
		Sprite left = game.getSprite(station.leftDisplayName);
		Drawable leftDraw;
		if(left != null) {
			leftDraw = new TextureRegionDrawable(left);
		} else {
			leftDraw = new TextureRegionDrawable(game.getSprite("craftingLeft"));
		}
		craftingLeft.setDrawable(leftDraw);
		
		Sprite right = game.getSprite(station.rightDisplayName);
		Drawable rightDraw;
		if(right != null) {
			rightDraw = new TextureRegionDrawable(right);
		} else {
			rightDraw = new TextureRegionDrawable(game.getSprite("craftingRight"));
		}
		craftingRight.setDrawable(rightDraw);
	}
    
}

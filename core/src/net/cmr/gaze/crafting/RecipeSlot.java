package net.cmr.gaze.crafting;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;

public class RecipeSlot extends ImageButton {

	/*public class RecipeGroup extends ButtonGroup<RecipeSlot> {
		
		Gaze game;
		
		public RecipeGroup(Gaze game, RecipeCategory category) {
			this.game = game;
			setMaxCheckCount(1);
			setMinCheckCount(0);
			for(Recipe recipe : category.recipes) {
				add(new RecipeSlot(game, recipe));
			}
		}
		
	}*/
	
	Gaze game;
	Recipe recipe;
	Item displayItem;
	
	public RecipeSlot(Gaze game, Recipe recipe, boolean filled) {
		this(getSlotBackground(game, filled),getSlotBackgroundChecked(game, filled));
		this.game = game;
		this.recipe = recipe;
		this.displayItem = Items.getItem(recipe.results[0], 1);
	}
	
	private RecipeSlot(Drawable background, Drawable checked) {
		super(background, background, checked);
	}

	private static TextureRegionDrawable background, backgroundChecked;
	private static TextureRegionDrawable backgroundFilled, backgroundCheckedFilled;
	
	private static Drawable getSlotBackground(Gaze game, boolean filled) {
		if(filled) {
			if(backgroundFilled == null) {
				backgroundFilled = new TextureRegionDrawable(game.getSprite("itemSlotBackgroundFilled"));
			}
			return backgroundFilled;
		} else {
			if(background == null) {
				background = new TextureRegionDrawable(game.getSprite("itemSlotBackground"));
			}
			return background;
		}
	}
	
	private static Drawable getSlotBackgroundChecked(Gaze game, boolean filled) {
		if(filled) {
			if(backgroundCheckedFilled == null) {
				backgroundCheckedFilled = new TextureRegionDrawable(game.getSprite("itemSlotBackgroundCheckedFilled"));
			}
			return backgroundCheckedFilled;
		} else {
			if(backgroundChecked == null) {
				backgroundChecked = new TextureRegionDrawable(game.getSprite("itemSlotBackgroundChecked"));
			}
			return backgroundChecked;
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		Item.draw(game, getStage().getViewport(), displayItem, batch, getX(), getY(), getWidth(), getHeight());
	}
	
}

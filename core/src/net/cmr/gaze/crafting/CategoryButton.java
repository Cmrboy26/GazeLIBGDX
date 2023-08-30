package net.cmr.gaze.crafting;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;

public class CategoryButton extends ImageButton {

	Gaze game;
	RecipeCategory category;
	Item displayItem;
	
	public CategoryButton(Gaze game, RecipeCategory category, boolean filled) {
		this(getSlotBackground(game, filled),getSlotBackgroundChecked(game, filled));
		this.game = game;
		this.category = category;
		this.displayItem = category.getCategoryItem();
	}
	
	private CategoryButton(Drawable background, Drawable checked) {
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
		Item.draw(game, null, displayItem, batch, getX(), getY(), getWidth(), getHeight());
	}

}

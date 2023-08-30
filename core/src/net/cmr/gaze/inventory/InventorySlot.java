package net.cmr.gaze.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.stage.GameScreen;

public class InventorySlot extends ImageButton {

	Gaze game;
	GameScreen screen;
	public int slot;
	
	public InventorySlot(Gaze game, GameScreen screen, int slot, boolean filled) {
		this(getSlotBackground(game, filled),getSlotBackgroundChecked(game, filled));
		this.game = game;
		this.screen = screen;
		this.slot = slot;
		/*this.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
				System.out.println(event);
				return false;
			}
		});*/
	}
	
	private InventorySlot(Drawable background, Drawable checked) {
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
		if(screen.getLocalPlayer()==null) {
			return;
		}
		super.draw(batch, parentAlpha);
		Item.draw(game, getStage().getViewport(), getInventory().get(slot), batch, getX(), getY(), getWidth(), getHeight());
		
		Vector2 mouseScreenPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		Vector2 mouseLocalPosition = this.screenToLocalCoordinates(mouseScreenPosition);
		if(hit(mouseLocalPosition.x, mouseLocalPosition.y, false) != null) {
			BitmapFont font = game.getFont(5f);
			Item item = getInventory().get(slot);
			if(getInventory().get(slot)!=null) {
				//font.draw(batch, Item.getName(item)+"\n"+Item.getDescription(item), getX()+mouseLocalPosition.x/*+getWidth()-5-1*/+getWidth()/4f, getY()+mouseLocalPosition.y/*+getHeight()/2-2*/);
			}
		}
	}
	
	public Inventory getInventory() {
		return screen.getLocalPlayer().getInventory();
	}
	
	public Item getItem() {
		return getInventory().get(slot);
	}
	
	public int getSlot() {
		return slot;
	}
	
	@Override
	public String toString() {
		return "["+slot+"]";
	}

	public boolean equals(Object object) {
		
		if(object instanceof InventorySlot) {
			InventorySlot slot = (InventorySlot) object;
			if(slot.slot==this.slot) {
				if(getInventory().equals(slot.getInventory())) {
					return true;
				}
			}
		}
		
		return false;
		
	}
	
}

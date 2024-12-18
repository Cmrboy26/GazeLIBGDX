package net.cmr.gaze.inventory;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.DataBuffer;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.stage.GameScreen;

public abstract class Item implements Cloneable {

	int size;
	ItemType type;
	
	int lastSize = -1;
	float sizeWidth = 0;
	
	public Item(ItemType type) {
		this.type = type;
		lastSize = -1;
	}
	public Item(ItemType type, int size) {
		this.type = type;
		this.size = size;
		lastSize = -1;
	}
	
	protected abstract void draw(Gaze game, Batch batch, float x, float y, float width, float height);
	
	public static void draw(Gaze game, Actor widget, Item item, Batch batch, float x, float y, float width, float height, boolean displayQuantity) {
		draw(game, widget, 0, item, batch, x, y, width, height, displayQuantity);
	}
	
	public static void draw(Gaze game, Actor widget, float hoverYOffset, Item item, Batch batch, float x, float y, float width, float height) {
		draw(game, widget, hoverYOffset, item, batch, x, y, width, height, true);
	}
	
	public static void viewportDraw(Gaze game, Viewport viewport, Item item, Batch batch, float x, float y, float width, float height) {
		standardDraw(game, item, batch, x, y, width, height, true);
		viewportHoverCheck(viewport, 0, item, x, y, width, height);
	}

	public static void draw(Gaze game, Actor widget, Item item, Batch batch, float x, float y, float width, float height) {
		draw(game, widget, 0, item, batch, x, y, width, height, true);
	}
	
	static GlyphLayout layout = new GlyphLayout();
	public static void draw(Gaze game, Actor widget, float hoverYOffset, Item item, Batch batch, float x, float y, float width, float height, boolean displayQuantity) {
		standardDraw(game, item, batch, x, y, width, height, displayQuantity);
		actorHoverCheck(widget, hoverYOffset, item, x, y, width, height);
	}
	private static void standardDraw(Gaze game, Item item, Batch batch, float x, float y, float width, float height, boolean displayQuantity) {
		if(item == null) {
			return;
		}
		item.draw(game, batch, x, y, width, height);

		if(item.getSize() != 1 && displayQuantity) {
			BitmapFont font = game.getFont((width/3f));
			layout.setText(font, item.getSize()+"");
			item.sizeWidth = layout.width;
			font.draw(batch, item.getSize()+"", x+width-item.sizeWidth-1, y+height/2-2);
		}
	}
	private static void actorHoverCheck(Actor widget, float hoverYOffset, Item item, float x, float y, float width, float height) {
		if(item == null) {
			return;
		}
		Stage stage;
		if(widget != null && ((stage = widget.getStage()).getViewport() != null)) {
			Vector2 mouseScreenPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY()+hoverYOffset);
			Vector2 mouseLocalPosition = widget.screenToLocalCoordinates(mouseScreenPosition);
			if(widget.hit(mouseLocalPosition.x, mouseLocalPosition.y, false) != null) {
				GameScreen.setHoveredItem(stage.getViewport(), item, mouseLocalPosition);
			}
		}
	}
	private static void viewportHoverCheck(Viewport viewport, float hoverYOffset, Item item, float x, float y, float width, float height) {
		if(item == null) {
			return;
		}
		if(viewport != null) {
			Vector2 mouseScreenPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY()+hoverYOffset);
			Vector2 mouseLocalPosition = viewport.unproject(new Vector2(mouseScreenPosition));
			Vector2 itemLocalPosition = new Vector2(x, y);
			if(itemLocalPosition.x <= mouseLocalPosition.x && itemLocalPosition.x + width >= mouseLocalPosition.x && itemLocalPosition.y <= mouseLocalPosition.y && itemLocalPosition.y + height >= mouseLocalPosition.y) {
				// OVER
				GameScreen.setHoveredItem(viewport, item, mouseLocalPosition);
			}
		}
	}
	
	public ItemType getType() {
		return type;
	}
	public int getSize() {
		return size;
	}
	
	
	public abstract Item readItem(DataInputStream input, ItemType type, int size) throws IOException;
	protected abstract void writeItem(ItemType type, DataBuffer buffer) throws IOException;
	
	public static void writeOutgoingItem(Item item, DataBuffer buffer) throws IOException {
		if(item == null) {
			buffer.writeInt(-1);
		} else {
			buffer.writeInt(item.getType().getID());
			buffer.writeInt(item.getSize());
			item.writeItem(item.getType(), buffer);
		}
	};
	public static Item readIncomingItem(DataInputStream input) throws IOException {
		int index = input.readInt();
		if(index == -1) {
			return null;
		}
		ItemType type = ItemType.getItemTypeFromID(index);
		return Items.getItem(type, input);
	};
	
	public static Item checkItem(Item item) {
		if(item == null) {
			return null;
		}
		if(item.getSize() <= 0) {
			return null;
		}
		
		return item;
	}
	
	public int add(int amount) {
		lastSize = size;
		this.size += amount;
		int returnvalue = Math.max(type.maxSize, size)-type.maxSize;
		size = Math.min(size, type.maxSize);
		return returnvalue;
	}
	public int subtract(int amount) {
		lastSize = size;
		this.size -= amount;
		int returnvalue = Math.abs(Math.min(0, this.size));
		this.size = Math.max(0, this.size);
		return returnvalue;
	}
	public Item set(int size) {
		lastSize = size;
		this.size = Math.min(size, type.maxSize);
		return this;
	}
	
	
	public Item addItem(Item item) {
		if(item==null) {
			return null;
		}
		if(!Objects.equals(item, this)) {
			return item;
		}
		int leftovers = add(item.getSize());
		item.set(leftovers);
		item = Item.checkItem(item);
		return item;
	}
	
	public Item removeItem(Item item) {
		if(item == null) {
			return null;
		}
		if(!Objects.equals(item, this)) {
			return item;
		}
		int notRemoved = subtract(item.getSize());
		item.set(notRemoved);
		item = Item.checkItem(item);
		return item;
	}
	
	public String getCraftSound() {
		return "craftSuccess";
	}

	public static String getName(Item item) {
		if(item==null) {
			return null;
		}
		String name = Items.nameMap.get(item.getType());
		if(name == null) {
			return item.getType().name();
		}
		return name;
	}
	public static String getDescription(Item item) {
		if(item==null) {
			return null;
		}
		String description = Items.descriptionMap.get(item.getType());
		if(description == null) {
			return "It's just "+item.getType().name();
		}
		return description; 
	}
	
	public Item clone() {
		try {
			return (Item) super.clone();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean equals(Object object) {
		if(object instanceof Item) {
			Item at = (Item) object;
			if(at.getType()==getType()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return type.name()+":"+getSize();
	}
	
}

package net.cmr.gaze.inventory;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Items.ItemType;

/**
 * A simple, abstract class that can be used for basic items (such as materials with no other functionality).
 */
public abstract class BasicItem extends Item {

    String itemString;
    public BasicItem(ItemType type, int size, String itemSprite) {
        super(type, size);
        this.itemString = itemSprite;
    }

    @Override
    protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
        batch.draw(game.getSprite(getItemString()), x, y, width, height);
    }

    public abstract Item getItem(int size);

    @Override
    public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
        return getItem(size);
    }

    @Override
    protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
        
    }
    
    public String getItemString() {
        return itemString;
    }

}

package net.cmr.gaze.inventory;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public abstract class CanisterItem extends BasicItem {

    int capacity;

    public CanisterItem(ItemType type, int size, String itemSprite) {
        super(type, size, itemSprite);
    }

    @Override
    protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
        buffer.writeInt(capacity);
    }

    @Override
    public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
        CanisterItem item = (CanisterItem) getItem(size);
        item.capacity = input.readInt();
        return item;
    }

    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
}

package net.cmr.gaze.inventory.custom;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class IronGear extends Item {

    public IronGear(int size) {
		super(ItemType.IRON_GEAR, size);
	}

    @Override
    protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getSprite("ironGear"), x, y, width, height);
    }

    @Override
    public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
        return new IronGear(size);
    }

    @Override
    protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
    }
    
}

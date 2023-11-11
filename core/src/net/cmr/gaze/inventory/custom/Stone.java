package net.cmr.gaze.inventory.custom;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class Stone extends BasicItem {

	public Stone(int size) {
		super(ItemType.STONE, size, "stone");
	}

	@Override
	public Item getItem(int size) {
		return new Stone(size);
	}
}

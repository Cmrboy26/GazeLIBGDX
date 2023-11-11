package net.cmr.gaze.inventory.custom;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.BasicItem;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;

public class CoalItem extends BasicItem {

    public CoalItem(int size) {
        super(ItemType.COAL, size, "coal");
    }

    @Override
    public Item getItem(int size) {
        return new CoalItem(size);
    }
}

package net.cmr.gaze.inventory.custom;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Placeable;
import net.cmr.gaze.world.TileType;

public class AcornItem extends Placeable {

    public AcornItem(int amount) {
        super(ItemType.ACORN, amount);
    }
    public AcornItem() {
        super(ItemType.ACORN, 1);
    }

    @Override
    public TileType getTileToPlace() {
        return TileType.TREE_SAPLING;
    }

    @Override
    protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getSprite("treeStage1"), x-width/2, y, width*2, height);
    }

    @Override
    public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
        return new AcornItem(size);
    }

    @Override
    protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
        
    }
    
}

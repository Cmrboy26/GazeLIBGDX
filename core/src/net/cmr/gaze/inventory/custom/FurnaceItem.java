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

public class FurnaceItem extends Placeable {

	public FurnaceItem(int amount) {
		super(ItemType.FURNACE, amount);
	}
	public FurnaceItem() {
		super(ItemType.FURNACE, 1);
	}

	@Override
	public TileType getTileToPlace() {
		return TileType.FURNACE;
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		//batch.draw(game.getSprite("furnace"), x+width*(1f/6f), y+height*(1f/12f), width*(2f/3f), height);
		batch.draw(game.getAnimation("furnace").getKeyFrame(0, true), x-width/2f, y-height/2f, width*2, height*2);
	}

	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new FurnaceItem(size);
	}

	@Override
	protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
		
	}

}

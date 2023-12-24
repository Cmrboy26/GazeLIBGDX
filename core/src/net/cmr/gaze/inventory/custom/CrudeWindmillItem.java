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

public class CrudeWindmillItem extends Placeable {

	public CrudeWindmillItem(int size) {
		super(ItemType.CRUDE_WINDMILL, size);
	}
	public CrudeWindmillItem() {
		super(ItemType.CRUDE_WINDMILL, 1);
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getAnimation("crudeWindmill").getKeyFrame(0), x-((width/1.5f)/2f-width/2f), y, width/1.5f, height);
	}

	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new CrudeWindmillItem(size);
	}

	@Override
	protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
		
	}

	@Override
	public TileType getTileToPlace() {
		return TileType.CRUDE_WINDMILL;
	}
	
}

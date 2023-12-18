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

public class BasicPumpItem extends Placeable {
    
    public BasicPumpItem(int size) {
		super(ItemType.BASIC_PUMP, size);
	}
	public BasicPumpItem() {
		super(ItemType.BASIC_PUMP, 1);
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getAnimation("basicPump1").getKeyFrame(0), x+(width/8f), y+(height/8f), width-(width/4f), height-(height/4f));
	}

	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new BasicPumpItem(size);
	}

	@Override
	protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
		
	}

	@Override
	public TileType getTileToPlace() {
		return TileType.BASIC_PUMP;
	}

}

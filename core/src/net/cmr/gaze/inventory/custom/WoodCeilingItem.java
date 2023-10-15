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

public class WoodCeilingItem extends Placeable {

	public WoodCeilingItem(int amount) {
		super(ItemType.WOOD_CEILING, amount);
	}
	
	public WoodCeilingItem() {
		super(ItemType.WOOD_CEILING, 1);
	}

	@Override
	public TileType getTileToPlace() {
		return TileType.WOOD_CEILING;
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getSprite("woodCeiling"), x+(width/3f), y-(height/12f), (width)/3f, height/1f);
	}

	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new WoodCeilingItem(size);
	}

	@Override
	protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
		
	}
}
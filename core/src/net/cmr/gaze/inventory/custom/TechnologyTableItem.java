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

public class TechnologyTableItem extends Placeable {

	public TechnologyTableItem(int amount) {
		super(ItemType.TECHNOLOGY_TABLE, amount);
	}
	public TechnologyTableItem() {
		super(ItemType.TECHNOLOGY_TABLE, 1);
	}

	@Override
	public TileType getTileToPlace() {
		return TileType.TECHNOLOGY_TABLE;
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getSprite("technologyTableItem"), x, y, width, height);
	}

	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new TechnologyTableItem(size);
	}

	@Override
	protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
		
	}

}

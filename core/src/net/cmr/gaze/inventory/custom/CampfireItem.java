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

public class CampfireItem extends Placeable {

	public CampfireItem(int amount) {
		super(ItemType.CAMPFIRE, amount);
	}
	public CampfireItem() {
		super(ItemType.CAMPFIRE, 1);
	}

	@Override
	public TileType getTileToPlace() {
		return TileType.CAMPFIRE;
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		//batch.draw(game.getSprite("campfire"), x+width*(1f/6f), y+height*(1f/12f), width*(2f/3f), height);
		batch.draw(game.getSprite("campfire"), x, y, width, height);
	}

	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new CampfireItem(size);
	}

	@Override
	protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
		
	}

}

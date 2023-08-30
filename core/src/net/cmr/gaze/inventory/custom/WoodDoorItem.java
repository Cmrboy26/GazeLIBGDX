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

public class WoodDoorItem extends Placeable {

	public WoodDoorItem(int amount) {
		super(ItemType.WOOD_DOOR, amount);
	}
	
	public WoodDoorItem() {
		super(ItemType.WOOD_DOOR, 1);
	}

	@Override
	public TileType getTileToPlace() {
		return TileType.WOOD_DOOR;
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getSprite("woodDoor1"), x+(width/3f), y-(height/12f), (width)/3f, height/1f);
	}

	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new WoodDoorItem(size);
	}

	@Override
	protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
		
	}

}

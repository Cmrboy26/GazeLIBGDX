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

public class StonePathFloorItem extends Placeable {

	public StonePathFloorItem(int amount) {
		super(ItemType.STONE_PATH_FLOOR, amount);
	}
	
	public StonePathFloorItem() {
		super(ItemType.STONE_PATH_FLOOR, 1);
	}

	@Override
	public TileType getTileToPlace() {
		return TileType.STONE_PATH_FLOOR;
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getSprite("stonePathFloor"), x+(width/8f), y+(height/8f), width-(width/4f), height-(height/4f));
	}

	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new StonePathFloorItem(size);
	}

	@Override
	protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
		
	}

}

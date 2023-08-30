package net.cmr.gaze.inventory.custom;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Tool;

public class WoodWateringCan extends Tool {

	public WoodWateringCan() {
		super(ItemType.WOOD_WATERING_CAN);
	}

	@Override
	public int breakLevel() {
		return 1;
	}

	@Override
	public double breakStrength() {
		return 1;
	}

	@Override
	public Material[] breakMaterials() {
		return null;
	}

	@Override
	public ToolType toolType() {
		return ToolType.WATERING_CAN;
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getSprite("woodWateringCan"), x, y, width, height);
	}

	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new WoodWateringCan();
	}

}

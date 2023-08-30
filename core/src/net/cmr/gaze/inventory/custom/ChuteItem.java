package net.cmr.gaze.inventory.custom;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Placeable;
import net.cmr.gaze.world.LightSource;
import net.cmr.gaze.world.TileType;

public class ChuteItem extends Placeable {

	public ChuteItem(int size) {
		super(ItemType.CHUTE, size);
	}
	public ChuteItem() {
		super(ItemType.CHUTE, 1);
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getSprite("chute"), x, y, width, height);
	}

	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new ChuteItem(size);
	}

	@Override
	protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
		
	}
	
	@Override
	public TileType getTileToPlace() {
		return TileType.CHUTE;
	}

}

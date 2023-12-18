package net.cmr.gaze.inventory.custom;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Placeable;
import net.cmr.gaze.world.LightSource;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.TileType;

public class SteamEngineItem extends Placeable {

	public SteamEngineItem(int size) {
		super(ItemType.STEAM_ENGINE, size);
	}
	public SteamEngineItem() {
		super(ItemType.STEAM_ENGINE, 1);
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getAnimation("steamEngine").getKeyFrame(0), x, y+height/5f, width, height/(3f/2f));
	}

	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new SteamEngineItem(size);
	}

	@Override
	protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
		
	}

	@Override
	public TileType getTileToPlace() {
		return TileType.STEAM_ENGINE;
	}
	
}

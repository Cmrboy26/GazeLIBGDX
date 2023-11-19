package net.cmr.gaze.inventory.custom;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.Color;
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
import net.cmr.gaze.world.tile.TorchTile;

public class TorchItem extends Placeable implements LightSource {

	public TorchItem(int size) {
		super(ItemType.TORCH, size);
	}
	public TorchItem() {
		super(ItemType.TORCH, 1);
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getAnimation("torch").getKeyFrame(0, true), x, y, width, height);
	}

	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new TorchItem(size);
	}

	@Override
	protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
		
	}

	@Override
	public TileType getTileToPlace() {
		return TileType.TORCH;
	}
	@Override
	public float getIntensity() {
		return 6f+TorchItem.getTorchPulse(this);
	}

	public Color getColor() {
		return TorchTile.TORCH_COLOR;
	}

	public static float getTorchPulse(Object object) {
		return MathUtils.sin(((Tile.tileRenderDelta*1.5f+((float) object.hashCode()/Integer.MAX_VALUE))%(MathUtils.PI*4f)-MathUtils.PI2))/3f;
	}
	
}

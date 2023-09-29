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

public class GrassSeeds extends Placeable {

    public GrassSeeds(int size) {
        super(ItemType.GRASS_SEEDS, size);
    }

    public GrassSeeds() {
        super(ItemType.GRASS_SEEDS, 1);
    }

    @Override
    public TileType getTileToPlace() {
        return TileType.TALL_GRASS;
    }

    @Override
    protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getSprite("unusedGrassSeeds"), x, y, width, height);
    }

    @Override
    public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
        return new GrassSeeds(size);
    }

    @Override
    protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {
        
    }
	
	@Override
	public String getPlaceAudio() {
		return "grassBreak";
	}
    
}

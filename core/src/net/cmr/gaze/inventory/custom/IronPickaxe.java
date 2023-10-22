package net.cmr.gaze.inventory.custom;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.graphics.g2d.Batch;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.inventory.Tool;

public class IronPickaxe extends Tool {

	public IronPickaxe() {
		super(ItemType.IRON_PICKAXE);
	}

	@Override
	public int breakLevel() {
		return 3;
	}

	@Override
	public double breakStrength() {
		return 1.25;
	}
	
	@Override
	public double breakSpeed() {
		return 1.35;
	}

	@Override
	public Material[] breakMaterials() {
		return new Material[] {Material.STONE, Material.IRON, Material.COPPER, Material.COAL};
	}
	
	@Override
	public ToolType toolType() {
		return ToolType.PICKAXE;
	}

	@Override
	protected void draw(Gaze game, Batch batch, float x, float y, float width, float height) {
		batch.draw(game.getSprite("ironPickaxe"), x, y, width, height);
	}
	
	@Override
	public Item readItem(DataInputStream input, ItemType type, int size) throws IOException {
		return new IronPickaxe();
	}

}

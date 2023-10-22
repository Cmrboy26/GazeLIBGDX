package net.cmr.gaze.inventory;

import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.inventory.Items.ItemType;

public abstract class Tool extends Item {
	
	public Tool(ItemType type) {
		super(type, 1);
	}
	
	public enum ToolType {
		AXE,
		PICKAXE, 
		SHOVEL,
		HOE, 
		WATERING_CAN,
	}
	
	public enum Material {
		WOOD,
		STONE,
		IRON, 
		COPPER,
	}

	public abstract int breakLevel();
	public abstract double breakStrength();
	public double breakSpeed() {
		return 1;
	}
	public abstract Material[] breakMaterials();
	public abstract ToolType toolType();

	@Override
	protected void writeItem(ItemType type, DataBuffer buffer) throws IOException {

	}

}

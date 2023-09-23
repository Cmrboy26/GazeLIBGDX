package net.cmr.gaze.inventory;

import com.badlogic.gdx.Input;

import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.world.World;

public abstract class FoodItem extends Item implements InteractiveItem {

	public FoodItem(ItemType type) {
		super(type);
	}
	public FoodItem(ItemType type, int quantity) {
		super(type, quantity);
	}
	
	public abstract int getFoodPoints();
	public abstract int getSaturationPoints();
	//public abstract Buff[] getBuffs();
	
	@Override
	public ItemInteraction onInteract(PlayerConnection connection, World world, int mouseButton, int x, int y) {
		if(mouseButton == 2) {
			return new ItemInteraction(true, -1);
		}
		return null;
	}
	

}

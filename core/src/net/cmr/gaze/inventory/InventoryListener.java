package net.cmr.gaze.inventory;

public class InventoryListener {
	
	public InventoryListener() {
		
	}
	
	public void onInventoryAction(InventoryListenerEvent event) {
		
	}
	
	public enum InventoryListenerEvent {
		
		ITEM_SET,
		ITEM_ADDED,
		ITEM_REMOVED,
		INVENTORY_OVERWRITTEN;
		
	}

}

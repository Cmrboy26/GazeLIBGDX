package net.cmr.gaze.inventory;

import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.world.World;

public interface InteractiveItem {
	
	public ItemInteraction onInteract(PlayerConnection connection, World world, int mouseButton, int x, int y);
	
	class ItemInteraction {
		
		public boolean actionOccured;
		public int itemChangeAmount;
		
		public ItemInteraction(boolean actionOccured) {
			this.actionOccured = actionOccured;
		}
		public ItemInteraction(boolean actionOccured, int itemChangeAmount) {
			this.actionOccured = actionOccured;
			this.itemChangeAmount = itemChangeAmount;
		}
		
	}
	
}

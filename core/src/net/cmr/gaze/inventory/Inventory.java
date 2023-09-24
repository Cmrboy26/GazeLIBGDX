package net.cmr.gaze.inventory;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.inventory.InventoryListener.InventoryListenerEvent;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.util.ArrayUtil;

public class Inventory {

	Item[] items;
	ArrayList<InventoryListener> listeners;
	
	public Inventory(int size) {
		items = new Item[size];
		listeners = new ArrayList<>();
	}
	
	public void writeInventory(DataBuffer buffer) throws IOException {
		buffer.writeInt(items.length);
		for(int i = 0; i < items.length; i++) {
			Item.writeOutgoingItem(items[i], buffer);
		}
	}
	public static Inventory readInventory(DataInputStream input) throws IOException {
		Inventory inventory = new Inventory(input.readInt());
		for(int i = 0; i < inventory.items.length; i++) {
			inventory.items[i] = Item.readIncomingItem(input);
		}
		return inventory;
	}
	
	public Item[] getAll() {
		return items;
	}
	
	public void put(int slot, Item item) {
		this.items[slot] = item;
		addEvent(InventoryListenerEvent.ITEM_SET);
	}
	public Item get(int slot) {
		return this.items[slot];
	}
	public void set(Item[] newItems) {
		items = newItems;
		addEvent(InventoryListenerEvent.INVENTORY_OVERWRITTEN);
	}
	
	public Item remove(Item remove) {
		for(int i = 0; i < items.length; i++) {
			if(remove == null) {
				return null;
			}
			if(items[i] != null) {
				remove = items[i].removeItem(remove);
				items[i] = Item.checkItem(items[i]);
				addEvent(InventoryListenerEvent.ITEM_REMOVED);
			}
		}
		return remove;
	}
	public Item remove(Item remove, int slot) {
		int i = slot;
		if(remove == null) {
			return null;
		}
		if(items[i] != null) {
			remove = items[i].removeItem(remove);
			items[i] = Item.checkItem(items[i]);
			addEvent(InventoryListenerEvent.ITEM_REMOVED);
		}
		return remove;
	}
	public Item add(Item add) {
		for(int i = 0; i < items.length; i++) {
			if(add == null) {
				return null;
			}
			if(items[i]!=null) {
				// will try to place items in stacks of the same type
				add = placeItem(add, i);
			}
		}
		for(int i = 0; i < items.length; i++) {
			if(add == null) {
				return null;
			}
			// will place in any available slot
			add = placeItem(add, i);
		}
		addEvent(InventoryListenerEvent.ITEM_ADDED);
		return Item.checkItem(add);
	}
	
	public Item placeItem(Item item, int slot) {
		item = Item.checkItem(item);
		if(item == null) {
			return null;
		}
		if(!isValidPlacement(item, slot)) {
			return item;
		}
		if(items[slot]==null) {
			items[slot] = Items.getItem(item.getType(), 0);
		}
		item = items[slot].addItem(item);
		return Item.checkItem(item);
	}
	
	public boolean isValidPlacement(Item item, int slot) {
		// can be overrided to only allow certain items to be placed in slots
		return true;
	}
	
	/**
	 * 
	 * @param selectedSlot Inventory slot of the selected slot
	 * @param clickedSlot Most recently clicked inventory slot 
	 * @param modifiers [0]: Input.Button value of the first selectedSlot click, [1] Input.Button of most recent action
	 * @return if the selectedSlot should be unselected or not.
	 */
	public boolean inventoryAction(Inventory selectedInventory, int selectedSlot, int clickedSlot, int[] modifiers) {
		
		Item selectedItem = selectedInventory.get(selectedSlot); // never null
		if(selectedItem == null) {
			return false;
			//throw new RuntimeException("Player clicked a slot while a null item was selected (client-server desync?)");
		}
		Item clickeditem = get(clickedSlot);
		
		if(modifiers[0]==Input.Buttons.LEFT&&modifiers[1]==Input.Buttons.LEFT) {
			// Transfer to other slot as much as possible
			
			if(selectedItem.equals(clickeditem)) {
				// Place as much as possible in the new slot
				Item leftovers = placeItem(selectedItem, clickedSlot);
				selectedInventory.put(selectedSlot, leftovers);
			} else {
				put(clickedSlot, selectedItem);
				selectedInventory.put(selectedSlot, clickeditem);
			}
			
		}
		
		// TODO: Right click should put one item in the clicked slot
		/*
		if(modifiers[0]==Input.Buttons.LEFT&&modifiers[1]==Input.Buttons.RIGHT) {
			if(selectedItem.equals(clickeditem)) {
				Item leftovers = placeItem(selectedItem, clickedSlot);
				selectedInventory.put(selectedSlot, leftovers);
				return selectedInventory.get(selectedSlot) == null;
			} else if(clickeditem == null) {
				
				return false;
			}
		}
		*/
		
		if(modifiers[0]==Input.Buttons.RIGHT&&modifiers[1]==Input.Buttons.LEFT) {
			if(clickeditem == null) {
				// Split the selectedItem
				// place it in the slot
			} else if(clickeditem.equals(selectedItem)) {
				// Split the selectedItem
				// place as much as possible to clickedslot
				// place leftovers back into selectedslot
			}
		}
		return true;
	}
	
	public int getQuantityOfItem(ItemType type) {
		int amount = 0;
		for(Item item : items) {
			if(item == null) {
				continue;
			}
			if(item.getType()==type) {
				amount+=item.getSize();
			}
		}
		return amount;
	}
	public HashMap<ItemType, Integer> getQuantitiesofItems() {
		HashMap<ItemType, Integer> returnValue = new HashMap<>();
		for(Item item : items) {
			if(item == null) {
				continue;
			}
			Integer at = returnValue.get(item.getType());
			if(at == null) {
				returnValue.put(item.getType(), item.getSize());
			} else {
				at+=item.getSize();
			}
		}
		return returnValue;
	}

	public void clear() {
		for(int i = 0; i < items.length; i++) {
			items[i] = null;
		}
	}
	
	public void addListener(InventoryListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	private void addEvent(InventoryListenerEvent itemSet) {
		for(int i = 0; i < listeners.size(); i++) {
			listeners.get(i).onInventoryAction(itemSet);
		}
	}
	
	@Override
	public String toString() {
		return ArrayUtil.toArrayString(items);
	}

	public int getSize() {
		return items.length;
	}
	
	
	
}

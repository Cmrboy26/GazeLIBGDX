package net.cmr.gaze.stage.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.Inventory;
import net.cmr.gaze.inventory.InventorySlot;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.stage.GameScreen;
import net.cmr.gaze.world.Tile;
import net.cmr.gaze.world.tile.ChestTile;

public class ChestInventoryWidget extends AbstractInventoryWidget {

	public ChestInventoryWidget(Gaze game, GameScreen screen) {
		super(game, screen, "chestInventory");
	}
	
	int x, y;
	
	public void setChestInventory(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Inventory getChestInventory() {
		Tile at = screen.tileDataObject.getTile(x, y, 1);
		if(at instanceof ChestTile) {
			ChestTile cst = (ChestTile) at;
			return cst.getInventory();
		}
		return null;
	}
	
	@Override
	public Table createInventoryTable() {
		Table inventoryTable = new Table();
		inventoryTable.setPosition(321, 192+58+14);
		inventoryTable.align(Align.top);
		
		final Inventory inventoryTest = new Inventory(21);
		inventoryTest.add(Items.getItem(ItemType.CHEST, 4));
		
		for(int i = 0; i < 7*3; i++) {
			InventorySlot button = new InventorySlot(game, screen, i, false) {
				@Override
				public Inventory getInventory() {
					return getChestInventory();
				}
			};
			//InventorySlot button = new InventorySlot(game, screen, i, false);
			inventoryGroup.add(button);
			if(i%7==0) {
				inventoryTable.row();
			}
			int bottomSpace = 2;
			if(i>=(7*2)) {
				bottomSpace = 14;
			}
			inventoryTable.add(button).width(18).height(18).spaceRight(2).spaceBottom(bottomSpace);
		}
		
		for(int i = 0; i < 7*5; i++) {
			
			int v = i;
			if(i >= 7*4) {
				v = i%7;
			} else {
				v += 7;
			}
			
			InventorySlot button = new InventorySlot(game, screen, v, false);
			inventoryGroup.add(button);
			
			if(i%7==0) {
				inventoryTable.row();
			}
			int bottomSpace = 2;
			if(i >= 7*3) {
				bottomSpace = 6;
			}
			
			inventoryTable.add(button).width(18).height(18).spaceRight(2).spaceBottom(bottomSpace);
		}
		return inventoryTable;
	}

}

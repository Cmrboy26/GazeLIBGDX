package net.cmr.gaze.stage.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import net.cmr.gaze.Gaze;
import net.cmr.gaze.inventory.InventorySlot;
import net.cmr.gaze.inventory.PlayerDisplayWidget;
import net.cmr.gaze.stage.GameScreen;

public class PlayerInventoryWidget extends AbstractInventoryWidget {

	public PlayerInventoryWidget(Gaze game, GameScreen screen) {
		super(game, screen, "inventory");
		
		float scale = 1.5f;
		float dim = (29*2*scale);
		float offset = ((dim/scale)-dim)/2f;
		PlayerDisplayWidget pdw = new PlayerDisplayWidget(game, screen);
		pdw.setBounds(320/2+40*2+offset, (360-256)/2+82*2+offset, dim, dim);
		addActor(pdw);
	}
	
	/*@Override
	public void act(float delta) {
		super.act(delta);
		if(inventoryGroup.selectedSlot!=null) {
			boolean over = false;
			for(InventorySlot slot : inventoryGroup.getButtons()) {
				if(slot.isOver()) {
					over = true;
					break;
				}
			}
			if(!over) {
				if(screen.overMenus(null))
			}
		}
	}*/
	
	@Override
	public Table createInventoryTable() {
		Table inventoryTable = new Table();
		inventoryTable.setPosition(321, 212);
		inventoryTable.align(Align.top);
		
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
			
			inventoryTable.add(button).width(22).height(22).spaceRight(2).spaceBottom(bottomSpace);
		}
		return inventoryTable;
	}

}

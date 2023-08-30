package net.cmr.gaze.inventory;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.cmr.gaze.networking.packets.InventoryClickPacket;
import net.cmr.gaze.stage.GameScreen;

public class InventoryGroup extends ButtonGroup<InventorySlot> {

	public InventoryGroup(GameScreen screen) {
		this.screen = screen;
	}
	
	GameScreen screen;
	public InventorySlot selectedSlot;
	int selectedButton;
	
	@Override
	public void add(InventorySlot button) {
		super.add(button);
		button.addListener(new ActorGestureListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int b) {
				super.touchUp(event, x, y, pointer, b);
				if(button.hit(x, y, false) != null) {
					changed(button, b);
				}
			}
		});
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//doubleCheckActor = actor;
				if(button.getItem()==null) {
					button.setChecked(false);
					if(actor.equals(selectedSlot)) {
						selectedSlot = null;
					}
				}
			}
		});
	}
	
	private void changed(InventorySlot clickedButton, int button) {
		
		if(button == Input.Buttons.MIDDLE) {
			return;
		}
		
		screen.game.playSound("tick", .5f);
		
		if(selectedSlot == null) {
			if(clickedButton.getItem()==null) {
				clickedButton.setChecked(false);
				return;
			}
			selectedSlot = clickedButton;
			selectedButton = button;
			selectedSlot.setChecked(true);
		} else {
			
			if(clickedButton.equals(selectedSlot)) {
				selectedSlot.setChecked(false);
				selectedSlot = null;
				clickedButton.setChecked(false);
				return;
			}
			
			boolean deselectSelected = clickedButton.getInventory().inventoryAction(selectedSlot.getInventory(), selectedSlot.slot, clickedButton.slot, new int[] {selectedButton, button});
			
			boolean selectedIsPlayerInventory = screen.getLocalPlayer()!=null&&screen.getLocalPlayer().getInventory().equals(selectedSlot.getInventory());
			boolean clickedIsPlayerInventory = screen.getLocalPlayer()!=null&&screen.getLocalPlayer().getInventory().equals(clickedButton.getInventory());
			selectedSlot.screen.sender.addPacket(new InventoryClickPacket(selectedIsPlayerInventory, clickedIsPlayerInventory, selectedSlot.slot, clickedButton.slot, new int[] {selectedButton, button}));
			
			if(deselectSelected) {
				selectedSlot.setChecked(false);
				selectedSlot = null;
			} else {
				selectedSlot.setChecked(true);
			}
			clickedButton.setChecked(false);
		}
	}
	
}

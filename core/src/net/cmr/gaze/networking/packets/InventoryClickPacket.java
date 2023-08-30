package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 15)
public class InventoryClickPacket extends Packet {

	boolean clickedIsPlayerInventory, selectedIsPlayerInventory;
	int selectedSlot, clickedSlot;
	int[] modifiers;
	
	public InventoryClickPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public InventoryClickPacket(boolean selectedIsPlayerInventory, boolean clickedIsPlayerInventory, int selectedSlot, int clickedSlot, int[] modifiers) {
		this.selectedIsPlayerInventory = selectedIsPlayerInventory;
		this.clickedIsPlayerInventory = clickedIsPlayerInventory;
		this.selectedSlot = selectedSlot;
		this.clickedSlot = clickedSlot;
		this.modifiers = modifiers;
		if(this.modifiers == null) {
			this.modifiers = new int[0];
		}
	}

	public boolean getSelectedIsPlayerInventory() {
		return selectedIsPlayerInventory;
	}
	public boolean getClickedIsPlayerInventory() {
		return clickedIsPlayerInventory;
	}
	public int getSelectedSlot() {
		return selectedSlot;
	}
	public int getClickedSlot() {
		return clickedSlot;
	}
	public int[] getModifiers() {
		return modifiers;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeBoolean(selectedIsPlayerInventory);
		buffer.writeBoolean(clickedIsPlayerInventory);
		buffer.writeInt(selectedSlot);
		buffer.writeInt(clickedSlot);
		buffer.writeInt(modifiers.length);
		for(int i = 0; i < modifiers.length; i++) {
			buffer.writeInt(modifiers[i]);
		}
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		selectedIsPlayerInventory = input.readBoolean();
		clickedIsPlayerInventory = input.readBoolean();
		selectedSlot = input.readInt();
		clickedSlot = input.readInt();
		modifiers = new int[input.readInt()];
		for(int i = 0; i < modifiers.length; i++) {
			modifiers[i] = input.readInt();
		}
	}

}

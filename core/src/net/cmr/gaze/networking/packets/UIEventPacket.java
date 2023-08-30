package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 20)
public class UIEventPacket extends Packet {

	public UIEventPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	boolean openState;
	short containerID;
	
	public UIEventPacket(boolean openState, int containerID) {
		this.openState = openState;
		this.containerID = (short) containerID;
	}
	
	public boolean getOpenState() {
		return openState;
	}
	/**
	 * 1: Crafting UI
	 * 2: Inventory UI
	 * @return Container ID of the changed state UI.
	 */
	public short getContainerID() {
		return containerID;
	}
	
	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeBoolean(openState);
		buffer.writeShort(containerID);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		openState = input.readBoolean();
		containerID = input.readShort();
	}
	
}

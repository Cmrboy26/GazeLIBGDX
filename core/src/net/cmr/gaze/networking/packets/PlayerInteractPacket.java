package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.networking.PlayerConnection;

@PacketID(id = 7)
public class PlayerInteractPacket extends Packet {

	boolean autorepeat;
	int clickType, worldX, worldY, modifier;
	
	public PlayerInteractPacket(boolean autorepeat, int clickType, int worldX, int worldY, int modifier) {
		this.autorepeat = autorepeat;
		this.clickType = clickType;
		this.worldX = worldX;
		this.worldY = worldY;
		this.modifier = modifier;
	}
	
	public PlayerInteractPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public boolean wasAutomaticallyRepeated() {
		return autorepeat;
	}
	public int getClickType() {
		return clickType;
	}
	public int getWorldX() {
		return worldX;
	}
	public int getWorldY() {
		return worldY;
	}
	/**
	 * For anything not requiring a modifier, the value will be -1
	 * For tile placing, the modifier will be the direction value if it is a rotatable object
	 */
	public int getModifier() {
		return modifier;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeBoolean(autorepeat);
		buffer.writeInt(clickType);
		buffer.writeInt(worldX);
		buffer.writeInt(worldY);
		buffer.writeInt(modifier);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		autorepeat = input.readBoolean();
		clickType = input.readInt();
		worldX = input.readInt();
		worldY = input.readInt();
		modifier = input.readInt();
	}

}

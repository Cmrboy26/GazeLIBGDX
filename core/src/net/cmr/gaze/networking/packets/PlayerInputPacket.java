package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 1)
public class PlayerInputPacket extends Packet {

	float x, y;
	boolean sprinting;
	
	public PlayerInputPacket(float x, float y, boolean sprinting) {
		this.x = x;
		this.y = y;
		this.sprinting = sprinting;
	}
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public boolean getSprinting() {
		return sprinting;
	}
	
	public PlayerInputPacket(DataInputStream stream, int packetSize) throws IOException {
		super(stream, packetSize);
	}
	
	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeFloat(x);
		buffer.writeFloat(y);
		buffer.writeBoolean(sprinting);
	}
	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		this.x = input.readFloat();
		this.y = input.readFloat();
		this.sprinting = input.readBoolean();
	}
	
	/*public static void writePacket(DataOutputStream out, short x, short y, boolean sprinting) throws IOException {
		DataBuffer buffer = new DataBuffer();
		
		buffer.writeShort(x);
		buffer.writeShort(y);
		buffer.writeBoolean(sprinting);
		
		sendPacketData(out, getIdentifier(), buffer);
	}
	
	public static PlayerInputPacket readPacket(DataInputStream input) throws IOException{
		return new PlayerInputPacket(input.readShort(), input.readShort(), input.readBoolean());
	}

	@Override
	public int getPacketIdentifier() {
		return getIdentifier();
	}
	
	public static short getIdentifier() {
		return 1;
	}*/
	
}

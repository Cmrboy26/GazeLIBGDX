package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 17)
public class AudioPacket extends Packet {

	String audioName;
	float volume = 1;
	float pitch = 1;
	int x = Integer.MAX_VALUE, y = Integer.MAX_VALUE;
	boolean isPositional = false;
	
	public AudioPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public AudioPacket(String audioName, float volume, float pitch, int x, int y) {
		this.audioName = audioName;
		this.volume = volume;
		this.pitch = pitch;
		this.x = x;
		this.y = y;
		isPositional = true;
	}
	public AudioPacket(String audioName, float volume, float pitch) {
		this.audioName = audioName;
		this.volume = volume;
		this.pitch = pitch;
	}
	public AudioPacket(String audioName, float volume) {
		this.audioName = audioName;
		this.volume = volume;
	}
	
	public float getPitch() {
		return pitch;
	}
	public String getAudio() {
		return audioName;
	}
	public float getVolume() {
		return volume;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public boolean isPositional() {
		return isPositional;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeUTF(audioName);
		buffer.writeFloat(volume);
		buffer.writeFloat(pitch);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeBoolean(isPositional);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		audioName = input.readUTF();
		volume = input.readFloat();
		pitch = input.readFloat();
		x = input.readInt();
		y = input.readInt();
		isPositional = input.readBoolean();
	}

}

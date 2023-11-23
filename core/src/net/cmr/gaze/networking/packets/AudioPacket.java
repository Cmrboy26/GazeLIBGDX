package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 17)
public class AudioPacket extends Packet {

	private String audioName;
	private float volume;
	private float pitch;
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
		this.isPositional = true;
	}
	public AudioPacket(String audioName, float volume, float pitch) {
		this.audioName = audioName;
		this.volume = volume;
		this.pitch = pitch;
		this.x = Integer.MAX_VALUE;
		this.y = Integer.MAX_VALUE;
		this.isPositional = false;
	}
	public AudioPacket(String audioName, float volume) {
		this.audioName = audioName;
		this.volume = volume;
		this.pitch = 1f;
		this.x = Integer.MAX_VALUE;
		this.y = Integer.MAX_VALUE;
		this.isPositional = false;
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

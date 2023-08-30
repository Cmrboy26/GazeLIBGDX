package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 17)
public class AudioPacket extends Packet {

	String audioName;
	float volume;
	
	public AudioPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public AudioPacket(String audioName, float volume) {
		this.audioName = audioName;
		this.volume = volume;
	}
	
	public String getAudio() {
		return audioName;
	}
	
	public float getVolume() {
		return volume;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeUTF(audioName);
		buffer.writeFloat(volume);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		audioName = input.readUTF();
		volume = input.readFloat();
	}

}

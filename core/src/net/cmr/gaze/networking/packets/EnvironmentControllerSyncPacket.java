package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.world.EnvironmentController;

@PacketID(id = 28)
public class EnvironmentControllerSyncPacket extends Packet {

	EnvironmentController env;

	public EnvironmentControllerSyncPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public EnvironmentControllerSyncPacket(EnvironmentController env) {
		this.env = env;
	}
	
	public EnvironmentController getEnvironmentController() {
		return env;
	}
	
	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		env.write(buffer);	
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		env = EnvironmentController.read(input);
	}
	
}

package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 16)
public class CraftPacket extends Packet {

	String category, recipe;
	int times;
	
	public CraftPacket(String category, String recipe, int times) {
		this.category = category;
		this.recipe = recipe;
		this.times = times;
	}
	
	public CraftPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getRecipe() {
		return recipe;
	}
	
	public int getTimes() {
		return times;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeUTF(category);
		buffer.writeUTF(recipe);
		buffer.writeInt(times);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		category = input.readUTF();
		recipe = input.readUTF();
		times = input.readInt();
	}

}

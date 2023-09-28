package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 25)
public class FoodPacket extends Packet {

    float hunger;
    float saturation;

    public FoodPacket(float hunger, float saturation) {
        this.hunger = hunger;
        this.saturation = saturation;
    }

    public FoodPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}

    public float getHunger() {
        return hunger;
    }
    public float getSaturation() {
        return saturation;
    }

    @Override
    protected void writePacketData(DataBuffer buffer) throws IOException {
        buffer.writeFloat(hunger);
        buffer.writeFloat(saturation);
    }

    @Override
    public void readPacketData(DataInputStream input, int packetSize) throws IOException {
        hunger = input.readFloat();
        saturation = input.readFloat();
    }
    
}

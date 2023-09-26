package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.game.ChatMessage;
import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;

@PacketID(id = 25)
public class ChatPacket extends Packet {

    ChatMessage message;

    public ChatPacket(ChatMessage message) {
        this.message = message;
    }

    public ChatPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}

    public ChatMessage getMessage() {
        return message;
    }

    @Override
    protected void writePacketData(DataBuffer buffer) throws IOException {
    	ChatMessage.write(message, buffer);
    }

    @Override
    public void readPacketData(DataInputStream input, int packetSize) throws IOException {
        message = ChatMessage.read(input);
    }
    
}

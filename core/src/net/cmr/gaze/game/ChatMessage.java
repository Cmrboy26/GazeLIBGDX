package net.cmr.gaze.game;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

import com.badlogic.gdx.utils.DataBuffer;

public class ChatMessage {
    
	public static final int MAX_HEADER_LENGTH = 16, MAX_MESSAGE_LENGTH = 100;
	
	String header, message;
	
	public ChatMessage(String header, String message) {
        header+="";
        message+="";
		this.header = header.substring(0, Math.min(header.length(), MAX_HEADER_LENGTH));
		this.message = message.substring(0, Math.min(message.length(), MAX_MESSAGE_LENGTH));
	}
	
    public static ChatMessage read(DataInputStream input) throws IOException {
        return new ChatMessage(input.readUTF(), input.readUTF());
    }

    public static void write(ChatMessage message, DataBuffer buffer) throws IOException {
    	Objects.requireNonNull(message);
        buffer.writeUTF(message.getHeader()+"");
        buffer.writeUTF(message.getMessage()+"");
    }
    
    public String getHeader() {
    	return header;
    }
    
    public String getMessage() {
    	return message;
    }

    public String toString() {
        return "<"+header+"> "+message;
    }

}

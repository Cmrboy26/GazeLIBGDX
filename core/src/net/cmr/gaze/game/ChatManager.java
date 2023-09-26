package net.cmr.gaze.game;

import java.util.LinkedList;

public class ChatManager {
    
    public LinkedList<ChatMessage> messages;
    public LinkedList<Long> messageRecievedTime;
    public static final int DEFAULT_CHAT_SIZE = 50;
    
    public ChatManager() {
    	messages = new LinkedList<>();
    	messageRecievedTime = new LinkedList<>();
    }
    
    public ChatMessage getMessage(int index) {
    	return messages.get(index);
    }
    
    // IN MILLISECONDS
    public long getMessageRecievedTime(int index) {
    	return messageRecievedTime.get(index);
    }
    
    public void addMessage(ChatMessage message) {
    	messages.addFirst(message);
    	messageRecievedTime.addFirst(System.currentTimeMillis());
    	while(messages.size() > DEFAULT_CHAT_SIZE) {
    		messages.removeLast();
    		messageRecievedTime.removeLast();
    	}
    }
    
}

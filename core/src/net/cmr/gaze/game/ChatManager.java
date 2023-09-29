package net.cmr.gaze.game;

import java.util.ArrayList;
import java.util.LinkedList;

public class ChatManager {
    
    public LinkedList<ChatMessage> messages;
    public LinkedList<Long> messageRecievedTime;
    public int count = 0;
    public static final int DEFAULT_CHAT_SIZE = 50;
    public ArrayList<ChatListener> listeners;
    
    public static abstract class ChatListener {
    	public abstract void onMessageRecieved(ChatMessage message);
    }

    public ChatManager() {
    	messages = new LinkedList<>();
    	messageRecievedTime = new LinkedList<>();
        listeners = new ArrayList<>();
    }
    
    public ChatMessage getMessage(int index) {
    	return messages.get(index);
    }
    
    // IN MILLISECONDS
    public long getMessageRecievedTime(int index) {
    	return messageRecievedTime.get(index);
    }
    
    public void addMessage(ChatMessage message) {
    	messages.addLast(message);
    	messageRecievedTime.addLast(System.currentTimeMillis());
    	while(messages.size() > DEFAULT_CHAT_SIZE) {
    		messages.removeFirst();
    		messageRecievedTime.removeFirst();
    	}
        count = messages.size();
        for(ChatListener listener : listeners) {
        	listener.onMessageRecieved(message);
        }
    }

    public void addListener(ChatListener listener) {
    	listeners.add(listener);
    }
    
}

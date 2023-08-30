package net.cmr.gaze.networking;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketSender {

	ConcurrentLinkedQueue<Packet> queue;
	
	public PacketSender() {
		queue = new ConcurrentLinkedQueue<>();
	}
	
	public void addPacket(Packet packet) {
		queue.add(packet);
	}
	
	public void sendPacketInstant(DataOutputStream output, Packet packet) {
		try {
			packet.sendPacket(output);
		} catch (IOException e) {
			
		}
	}
	
	public void sendAll(DataOutputStream output) {
		while(queue.size() > 0) {
			Packet p = queue.poll();
			try {
				p.sendPacket(output);
			} catch(IOException e) {
				
			}
		}
	}
	
}

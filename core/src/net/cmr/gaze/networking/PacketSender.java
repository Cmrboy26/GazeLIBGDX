package net.cmr.gaze.networking;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.cmr.gaze.debug.RateCalculator;

public class PacketSender {

	ConcurrentLinkedQueue<Packet> queue;
	RateCalculator attachedCalculator;
	
	public PacketSender() {
		queue = new ConcurrentLinkedQueue<>();
	}
	
	public void attatchCalculator(RateCalculator rateCalculator) {
		this.attachedCalculator = rateCalculator;
	}
	
	public void addPacket(Packet packet) {
		queue.add(packet);
	}
	
	public void sendPacketInstant(DataOutputStream output, Packet packet) {
		try {
			int size = packet.sendPacket(output);
			if(attachedCalculator!=null) {
				attachedCalculator.add(size, System.currentTimeMillis());
			}
		} catch (IOException e) {
			
		}
	}
	
	public void sendAll(DataOutputStream output) {
		int size = 0;
		while(queue.size() > 0) {
			Packet p = queue.poll();
			try {
				size += p.sendPacket(output);
				
			} catch(IOException e) {
				
			}
		}
		if(attachedCalculator!=null) {
			attachedCalculator.add(size, System.currentTimeMillis());
		}
	}
	
}

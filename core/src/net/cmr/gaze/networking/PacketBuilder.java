package net.cmr.gaze.networking;

import java.io.DataInputStream;
import java.io.IOException;

import net.cmr.gaze.debug.RateCalculator;
import net.cmr.gaze.leveling.SkillsPacket;
import net.cmr.gaze.networking.packets.AudioPacket;
import net.cmr.gaze.networking.packets.AuthenticationPacket;
import net.cmr.gaze.networking.packets.ChatPacket;
import net.cmr.gaze.networking.packets.ChestInventoryPacket;
import net.cmr.gaze.networking.packets.ChunkDataPacket;
import net.cmr.gaze.networking.packets.ChunkUnloadPacket;
import net.cmr.gaze.networking.packets.CraftPacket;
import net.cmr.gaze.networking.packets.CraftingStationPacket;
import net.cmr.gaze.networking.packets.DespawnEntity;
import net.cmr.gaze.networking.packets.DisconnectPacket;
import net.cmr.gaze.networking.packets.EntityPositionsPacket;
import net.cmr.gaze.networking.packets.FoodPacket;
import net.cmr.gaze.networking.packets.HealthPacket;
import net.cmr.gaze.networking.packets.HotbarUpdatePacket;
import net.cmr.gaze.networking.packets.InventoryClickPacket;
import net.cmr.gaze.networking.packets.InventoryUpdatePacket;
import net.cmr.gaze.networking.packets.PingPacket;
import net.cmr.gaze.networking.packets.PlayerConnectionStatusPacket;
import net.cmr.gaze.networking.packets.PlayerInputPacket;
import net.cmr.gaze.networking.packets.PlayerInteractPacket;
import net.cmr.gaze.networking.packets.PositionPacket;
import net.cmr.gaze.networking.packets.QuestDataPacket;
import net.cmr.gaze.networking.packets.SpawnEntity;
import net.cmr.gaze.networking.packets.TileUpdatePacket;
import net.cmr.gaze.networking.packets.UIEventPacket;
import net.cmr.gaze.networking.packets.WorldChangePacket;

public abstract class PacketBuilder {
	
	int identifier = -1;
	int nextPacketSize = -1;
	int lastAvailable = -1;
	int maxIterations = 60;
	public static final int MAX_PACKET_SIZE = 8192;
	
	public long millisTimeSinceDataRecieved;
	boolean serverSide; // used for debug purposes
	RateCalculator attachedCalculator;
	
	// packet format:
	/* 
	  [ identifier *2 ][ int size *4 ][ content *size ]
	 */
	
	public PacketBuilder(boolean serverSide) {
		millisTimeSinceDataRecieved = System.currentTimeMillis();
		this.serverSide = serverSide;
	}
	
	public PacketBuilder(boolean serverSide, int maxIterations) {
		this.maxIterations = maxIterations;
		this.serverSide = serverSide;
	}
	
	public void attatchCalculator(RateCalculator rateCalculator) {
		this.attachedCalculator = rateCalculator;
	}
	
	public void build(DataInputStream input) throws Exception {
		for(int i = 0; i < maxIterations && input.available()!=0; i++) {
			if(lastAvailable == -1) {
				millisTimeSinceDataRecieved = System.currentTimeMillis();
			}
			if(lastAvailable != input.available()) {
				millisTimeSinceDataRecieved = System.currentTimeMillis();
			}
			
			if(!packetHeaderRecieved() && input.available() >= 8) {
				identifier = input.readInt();
				nextPacketSize = input.readInt();
				if(nextPacketSize >= MAX_PACKET_SIZE) {
					throw new IOException("Max packet size exceeded! Packet ID: "+identifier+", expected size: "+nextPacketSize);
				}
			}
			
			if(!packetHeaderRecieved()) {
				break;
			}
			
			if(attachedCalculator!=null) {
				attachedCalculator.add(nextPacketSize, System.currentTimeMillis());
			}

			//System.out.println("[DEBUG] Reading packet: [SERVER:"+serverSide+","+identifier+":"+nextPacketSize+"]");
			if(input.available()>=nextPacketSize) {
				switch(identifier) {
				case 0: {
					processPacket(new AuthenticationPacket(input, nextPacketSize));
					break;
				}
				case 1: {
					processPacket(new PlayerInputPacket(input, nextPacketSize));
					break;
				}
				case 2: {
					processPacket(new PingPacket(input, nextPacketSize));
					break;
				}
				case 3: {
					processPacket(new DisconnectPacket(input, nextPacketSize));
					break;
				}
				case 4: {
					processPacket(new PositionPacket(input, nextPacketSize));
					break;
				}
				case 5: {
					processPacket(new PlayerConnectionStatusPacket(input, nextPacketSize));
					break;
				}
				case 6: {
					processPacket(new ChunkDataPacket(input, nextPacketSize));
					break;
				}
				case 7: {
					processPacket(new PlayerInteractPacket(input, nextPacketSize));
					break;
				}
				case 8: {
					processPacket(new TileUpdatePacket(input, nextPacketSize));
					break;
				}
				case 9: {
					processPacket(new ChunkUnloadPacket(input, nextPacketSize));
					break;
				}
				case 10: {
					processPacket(new SpawnEntity(input, nextPacketSize));
					break;
				}
				case 11: {
					processPacket(new DespawnEntity(input, nextPacketSize));
					break;
				}
				case 12: {
					processPacket(new EntityPositionsPacket(input, nextPacketSize));
					break;
				}
				case 13: {
					processPacket(new InventoryUpdatePacket(input, nextPacketSize));
					break;
				}
				case 14: {
					processPacket(new HotbarUpdatePacket(input, nextPacketSize));
					break;
				}
				case 15: {
					processPacket(new InventoryClickPacket(input, nextPacketSize));
					break;
				}
				case 16: {
					processPacket(new CraftPacket(input, nextPacketSize));
					break;
				}
				case 17: {
					processPacket(new AudioPacket(input, nextPacketSize));
					break;
				}
				case 18: {
					processPacket(new WorldChangePacket(input, nextPacketSize));
					break;
				}
				case 19: {
					processPacket(new CraftingStationPacket(input, nextPacketSize));
					break;
				}
				case 20: {
					processPacket(new UIEventPacket(input, nextPacketSize));
					break;
				}
				case 21: {
					processPacket(new SkillsPacket(input, nextPacketSize));
					break;
				}
				case 22: {
					processPacket(new ChestInventoryPacket(input, nextPacketSize));
					break;
				}
				case 23: {
					processPacket(new QuestDataPacket(input, nextPacketSize));
					break;
				}
				case 24: {
					processPacket(new HealthPacket(input, nextPacketSize));
					break;
				}
				case 25: {
					processPacket(new FoodPacket(input, nextPacketSize));
					break;
				}
				case 26: {
					processPacket(new ChatPacket(input, nextPacketSize));
					break;
				}
				default: {
					throw new NullPointerException("Unknown packet recieved with ID: "+identifier);
				}
				}
				identifier = -1;
				nextPacketSize = -1;
			} else {
				lastAvailable = input.available();
				break;
			}
			lastAvailable = input.available();
		}
	}
	
	public abstract void processPacket(Packet packet);
	
	private boolean packetHeaderRecieved() {
		return identifier != -1 && nextPacketSize != -1;
	}
	
	// read until
	
	/*public byte[] getBytes(byte[] buffer, int index, int desiredQuantity) {
		int i = index;
		byte[] returnarray = new byte[desiredQuantity];
		if(index+desiredQuantity<buffer.length) {
			for(;i<index+desiredQuantity;i++) {
				returnarray[i-index] = buffer[i];
			}
			return returnarray;
		} else {
			return null;
		}
	}*/
	
	
	
	/*public Optional<Byte> getNextByte(byte[] buffer, int index) {
		Optional<Byte> returnval = Optional.empty();
		if(index < buffer.length) {
			returnval = Optional.of(buffer[index]);
		}
		return returnval;
		
	}*/
	
}

package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.inventory.Inventory;
import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.util.UuidUtils;
import net.cmr.gaze.world.entities.Player;

@PacketID(id = 13)
public class InventoryUpdatePacket extends Packet {

	Inventory inventory;
	UUID entityID;
	
	public InventoryUpdatePacket(Player player) {
		this.inventory = player.getInventory();
		this.entityID = player.getUUID();
	}
	
	public InventoryUpdatePacket(DataInputStream input, int nextPacketSize) throws IOException {
		super(input, nextPacketSize);
	}

	public UUID getUUID() {
		return entityID;
	}
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeInt(UuidUtils.asBytes(entityID).length);
		buffer.write(UuidUtils.asBytes(entityID));
		inventory.writeInventory(buffer);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		byte[] id = new byte[input.readInt()];
		input.read(id);
		entityID = UuidUtils.asUuid(id);
		inventory = Inventory.readInventory(input);
	}

	
}

package net.cmr.gaze.leveling;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.util.UuidUtils;
import net.cmr.gaze.world.entities.Player;

@PacketID(id = 21)
public class SkillsPacket extends Packet {

	public SkillsPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}

	Skills skills;
	UUID playerUUID;
	
	public SkillsPacket(Skills skills, Player player) {
		this.skills = skills;
		this.playerUUID = player.getUUID();
	}
	
	public Skills getSkills() {
		return skills;
	}
	
	public UUID getUUID() {
		return playerUUID;
	}
	
	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		skills.writeSkills(buffer);
		UuidUtils.writeUUID(buffer, playerUUID);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		skills = Skills.readSkills(input);
		playerUUID = UuidUtils.readUUID(input);
	}
	
}

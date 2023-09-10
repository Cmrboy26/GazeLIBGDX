package net.cmr.gaze.networking.packets;

import java.io.DataInputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.networking.Packet;
import net.cmr.gaze.networking.PacketID;
import net.cmr.gaze.stage.widgets.QuestBook.Quest;

@PacketID(id = 23)
public class QuestDataPacket extends Packet {

	Quest quest;
	int questNumber, tier;
	boolean value;
	
	public QuestDataPacket(DataInputStream input, int packetSize) throws IOException {
		super(input, packetSize);
	}
	
	public QuestDataPacket(Quest quest, int questNumber, int tier, boolean value) {
		this.quest = quest;
		this.questNumber = questNumber;
		this.tier = tier;
		this.value = value;
	}
	
	public Quest getQuest() {
		return quest;
	}
	public int getQuestNumber() {
		return questNumber;
	}
	public int getQuestTier() {
		return tier;
	}
	public boolean getValue() {
		return value;
	}
	
	@Override
	protected void writePacketData(DataBuffer buffer) throws IOException {
		buffer.writeInt(quest.id);
		buffer.writeInt(questNumber);
		buffer.writeInt(tier);
		buffer.writeBoolean(value);
	}

	@Override
	public void readPacketData(DataInputStream input, int packetSize) throws IOException {
		quest = Quest.getQuestFromID(input.readInt());
		questNumber = input.readInt();
		tier = input.readInt();
		value = input.readBoolean();
	}

}

package net.cmr.gaze.stage.widgets;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.stage.widgets.QuestBook.Quests;

public class QuestData {
	
	HashMap<Quests, Boolean[][]> data;
	public static HashMap<Integer, Quests> questsMap = null;
	
	public QuestData() {
		data = new HashMap<>();
	}
	
	public void write(DataBuffer buffer) throws IOException {
		int totalBooleans = 0;
		for(Quests quest : Quests.values()) {
			totalBooleans+=(quest.getSize()*3);
		}
		buffer.writeInt(totalBooleans);
		for(Quests quest : Quests.values()) {
			Boolean[][] tempData = data.get(quest);
			for(int i = 0; i < quest.getSize(); i++) {
				buffer.writeInt(quest.id);
				buffer.writeBoolean(tempData[i][0]);
				buffer.writeBoolean(tempData[i][1]);
				buffer.writeBoolean(tempData[i][2]);
			}
		}
	}
	
	public static QuestData read(DataInputStream input) throws IOException {
		QuestData qd = new QuestData();
		int totalBooleans = input.readInt();
		
		HashMap<Quests, ArrayList<Boolean>> tempQuestMap = new HashMap<>();
		
		for(Quests quest : Quests.values()) {
			tempQuestMap.put(quest, new ArrayList<>());
		}
		
		while(totalBooleans > 0) {
			Quests quest = Quests.getQuestFromID(input.readInt());
			boolean bronze = input.readBoolean();
			boolean silver = input.readBoolean();
			boolean gold = input.readBoolean();
			tempQuestMap.get(quest).add(bronze);
			tempQuestMap.get(quest).add(silver);
			tempQuestMap.get(quest).add(gold);
			totalBooleans-=3;
		}
		
		HashMap<Quests, Boolean[][]> data = new HashMap<>();
		
		for(Quests quest : Quests.values()) {
			int v = 0;
			ArrayList<Boolean> temp = tempQuestMap.get(quest);
			Boolean[][] list = new Boolean[quest.getSize()][3];
			for(int i = 0; i < quest.getSize(); i++) {
				list[i][0] = temp.get(v);
				list[i][1] = temp.get(v+1);
				list[i][2] = temp.get(v+2);
				v+=3;
			}
			data.put(quest, list);
		}
		qd.setData(data);
		
			
		return qd;
	}
	
	public void setData(HashMap<Quests, Boolean[][]> data) {
		this.data = data;
	}
	
}

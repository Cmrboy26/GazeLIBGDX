package net.cmr.gaze.stage.widgets;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.stage.widgets.QuestBook.Quest;

public class QuestData {
	
	private HashMap<Quest, Boolean[][]> data;
	public static HashMap<Integer, Quest> questsMap = null;
	
	public QuestData() {
		data = new HashMap<>();
		for(Quest quest : Quest.values()) {
			Boolean[][] value = new Boolean[quest.getSize()][3];
			for(int x = 0; x < value.length; x++) {
				for(int y = 0; y < 3; y++) {
					value[x][y] = false;
				}
			}
			data.put(quest, value);
		}
	}
	
	public static void write(QuestData qdata, DataBuffer buffer) throws IOException {
		if(qdata==null) {
			buffer.writeInt(-1);
			return;
		}
		int totalBooleans = 0;
		for(Quest quest : Quest.values()) {
			totalBooleans+=(quest.getSize()*3);
		}
		buffer.writeInt(totalBooleans);
		for(Quest quest : Quest.values()) {
			Boolean[][] tempData = qdata.data.get(quest);
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
		
		if(totalBooleans==-1) {
			return qd;
		}
		
		HashMap<Quest, ArrayList<Boolean>> tempQuestMap = new HashMap<>();
		
		for(Quest quest : Quest.values()) {
			tempQuestMap.put(quest, new ArrayList<>());
		}
		
		while(totalBooleans > 0) {
			Quest quest = Quest.getQuestFromID(input.readInt());
			boolean bronze = input.readBoolean();
			boolean silver = input.readBoolean();
			boolean gold = input.readBoolean();
			tempQuestMap.get(quest).add(bronze);
			tempQuestMap.get(quest).add(silver);
			tempQuestMap.get(quest).add(gold);
			totalBooleans-=3;
		}
		
		HashMap<Quest, Boolean[][]> data = new HashMap<>();
		
		for(Quest quest : Quest.values()) {
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
	
	public void setData(HashMap<Quest, Boolean[][]> data) {
		this.data = data;
	}
	
	public HashMap<Quest, Boolean[][]> getData() {
		return data;
	}
	
	public String toString() {
		
		String end = "";
		
		for(Quest quest : Quest.values()) {
			Boolean[][] map = data.get(quest);
			for(int i = 0; i < quest.getSize(); i++) {
				end+=quest.name()+" | "+quest.getTitle(i)+"\t {"+map[i][0]+", "+map[i][0]+", "+map[i][0]+"}\n";
			}
		}
		
		return end;
		
	}
	
}

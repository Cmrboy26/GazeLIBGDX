package net.cmr.gaze.quests;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.badlogic.gdx.utils.DataBuffer;

import net.cmr.gaze.quests.Quests.QuestTier;
import net.cmr.gaze.util.Identifier;

public class QuestsData {
    
    // questData is a map of quest identifiers to a boolean array of size 3
    // the index represents the quest tier, and the boolean represents whether or not the quest is completed
    public HashMap<Identifier, Boolean[]> questData;
    public static final int VERSION = 0;

    public QuestsData() {
        questData = new HashMap<>();
    }

    public Boolean[] getDataArray(Identifier id) {
        Boolean[] end = questData.get(id);
        if(end == null) {
            end = new Boolean[] {false, false, false};
            questData.put(id, end);
        }
        return end;
    }

    public void setQuestCompleted(Identifier id, QuestTier tier, boolean completed) {
        Boolean[] data = getDataArray(id);
        data[tier.tier] = completed;
    }
    public boolean isQuestCompleted(Identifier id, QuestTier tier) {
        return getDataArray(id)[tier.tier];
    }

    public static void write(QuestsData qdata, DataBuffer buffer) throws IOException {
        if(qdata==null) {
            buffer.writeInt(-1);
            return;
        }
        buffer.writeInt(VERSION);
        buffer.writeInt(qdata.questData.size());
        for(Identifier id : qdata.questData.keySet()) {
            buffer.writeUTF(id.getID());
            Boolean[] data = qdata.getDataArray(id);
            for(int i = 0; i < 3; i++) {
                buffer.writeBoolean(data[i]);
            }
        }
    }

    public QuestsData read(DataInputStream input) throws IOException {
        QuestsData qd = new QuestsData();
        int version = input.readInt();
        if(version==-1) {
            return qd;
        }
        switch(version) {
            case(-1): {
                return qd;
            }
            case(0): {
                int size = input.readInt();
                for(int i = 0; i < size; i++) {
                    Identifier id = new Identifier(input.readUTF());
                    Boolean[] data = new Boolean[3];
                    for(int j = 0; j < 3; j++) {
                        data[j] = input.readBoolean();
                    }
                    qd.questData.put(id, data);
                }
                break;
            }
            default: {
                throw new IOException("Unknown QuestsData version: "+version);
            }
        }

        return qd;
    }

}

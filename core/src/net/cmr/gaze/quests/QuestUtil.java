package net.cmr.gaze.quests;

import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.leveling.Skills.Skill;
import net.cmr.gaze.networking.PlayerConnection.QuestCheckType;

public class QuestUtil {
    
    public static boolean pickup(QuestCheckType eventType, ItemType checkType, Object[] args) {
		if(eventType == QuestCheckType.PICKUP) {
            for(ItemType type : ((ItemType[]) args)) {
                if(checkType==type) {
                    return true;
                }
            }
		}
		return false;
	}

    public static boolean craft(QuestCheckType eventType, ItemType checkType, Object[] args) {
        if(eventType == QuestCheckType.CRAFT) {
            for(ItemType type : ((ItemType[]) args)) {
                if(checkType==type) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean levelUp(QuestCheckType eventType, Skill checkSkill, int minimumLevel, Object[] args) {
        if(eventType == QuestCheckType.LEVELUP) {
            Skill skill = (Skill) args[0];
            int level = (int) args[1];
            if(checkSkill.equals(skill) && level>=minimumLevel) {
                return true;
            }
        }
        return false;
    }

}

package net.cmr.gaze.quests;

import java.util.HashMap;

import net.cmr.gaze.networking.PlayerConnection;
import net.cmr.gaze.networking.PlayerConnection.QuestCheckType;
import net.cmr.gaze.quests.Quests.QuestTier;
import net.cmr.gaze.util.Identifier;

public class QuestCategory {

	Identifier id;
	String name;
	HashMap<Identifier, QuestPage> questPages;
	
	public QuestCategory(Identifier id, String name) {
		this.id = id;
		this.name = name;
		this.questPages = new HashMap<>();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public QuestPage getQuestPage(Identifier id) {
		return this.questPages.get(id);
	}
	
	public class QuestPage {
		
		Identifier id;
		String name, description;
		QuestObject[] quests;
		
		public QuestPage(Identifier id, String name) {
			this.id = id;
			this.name = name;
			this.quests = new QuestObject[3];
			questPages.put(id, this);
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public String getName() {
			return name;
		}
		public QuestObject getQuest(QuestTier tier) {
			return quests[tier.tier];
		}
		
		public abstract class QuestObject {
			
			final QuestTier tier;
			String description;
			QuestCheckType[] questCheckTypes;
			
			public QuestObject(QuestTier tier, String description, QuestCheckType... questCheckTypes) {
				this.tier = tier;
				this.description = description;
				this.questCheckTypes = questCheckTypes;
				quests[tier.tier] = this;
			}
			
			public void setDescription(String description) {
				this.description = description;
			}
			public String getDescription() {
				return description;
			}
			public QuestTier getTier() {
				return tier;
			}
			public boolean isQuestCheckType(QuestCheckType type) {
				for(QuestCheckType t : questCheckTypes) {
					if(t == type) {
						return true;
					}
				}
				return false;
			}
			
			public abstract boolean questCheck(PlayerConnection connection, QuestCheckType type, Object... args);
		}
		
	}
	
}

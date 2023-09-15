package net.cmr.gaze.quests;

import java.util.HashMap;

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
	
	class QuestPage {
		
		Identifier id;
		String name, description;
		Quest[] quests;
		
		public QuestPage(Identifier id, String name) {
			this.id = id;
			this.name = name;
			this.quests = new Quest[3];
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
		public Quest getQuest(QuestTier tier) {
			return quests[tier.tier];
		}
		
		abstract class Quest {
			
			final QuestTier tier;
			String description;
			
			public Quest(QuestTier tier, String description) {
				this.tier = tier;
				this.description = description;
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
			
			public abstract boolean isQuestCompleted
			
		}
		
	}
	
}
